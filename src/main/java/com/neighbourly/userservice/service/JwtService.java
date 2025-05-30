package com.neighbourly.userservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import io.jsonwebtoken.security.SignatureException;



@Service
public class JwtService {
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    public String generateToken(Long userId, String email, Map<String, List<String>> roles) {
        logger.info("Generating token for userId: {}, email: {}, roles: {}", userId, email, roles);
        try {
            String token = Jwts.builder()
                    .setSubject(email)
                    .claim("userId", userId)
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
            logger.info("Generated token successfully for userId: {}", userId);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for userId: {}, error: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to generate token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            logger.info("Token validated successfully");
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token structure: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.info("Extracted email: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Error extracting email from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract email", e);
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Long userId = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Long.class);
            logger.info("Extracted userId: {}", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Error extracting userId from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract userId", e);
        }
    }

    public Map<String, List<String>> getRolesFromToken(String token) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, List<String>> roles = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("roles", Map.class);
            logger.info("Extracted roles: {}", roles);
            return roles;
        } catch (Exception e) {
            logger.error("Error extracting roles from token: {}", e.getMessage());
            throw new RuntimeException("Failed to extract roles", e);
        }
    }
}
