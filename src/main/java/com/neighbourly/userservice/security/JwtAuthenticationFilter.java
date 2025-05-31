package com.neighbourly.userservice.security;

import com.neighbourly.userservice.security.CustomPrincipal;
import com.neighbourly.userservice.security.SecurityConstants;
import com.neighbourly.userservice.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = null;
        String authHeader = request.getHeader(SecurityConstants.HEADER_STRING);
        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
            token = authHeader.substring(7);
            logger.info("Token extracted from Authorization header: {}", token);
        } else {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwtToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        logger.info("Token extracted from cookie: {}", token);
                        break;
                    }
                }
            }
        }

        if (token != null && jwtService.validateToken(token)) {
            String email = jwtService.getEmailFromToken(token);
            Long userId = jwtService.getUserIdFromToken(token);
            Map<String, List<String>> roles = jwtService.getRolesFromToken(token);
            List<String> authorities = roles.entrySet().stream()
                    .flatMap(entry -> entry.getValue().stream()
                            .map(role -> "ROLE_" + entry.getKey().toUpperCase() + "_" + role.toUpperCase()))
                    .collect(Collectors.toList());
            logger.info("Authenticated user - Email: {}, UserId: {}, Authorities: {}", email, userId, authorities);
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    new CustomPrincipal(userId, email, roles.entrySet().stream()
                            .flatMap(e -> e.getValue().stream()).collect(Collectors.toList())),
                    null,
                    authorities.stream()
                            .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role))
                            .collect(Collectors.toList())
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            logger.warn("No valid token found in request");
        }
        filterChain.doFilter(request, response);
    }
}