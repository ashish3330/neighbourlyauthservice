package com.neighbourly.userservice.service;

import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public TokenService(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    public String refreshToken(String token) {
        logger.info("Refreshing token");
        if (!jwtService.validateToken(token)) {
            logger.warn("Invalid token for refresh");
            throw new IllegalArgumentException("Invalid or expired token");
        }
        String email = jwtService.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("User not found for email: {}", email);
                    return new RuntimeException("User not found");
                });
        String newToken = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
        logger.info("Generated new token for user: {}", email);
        return newToken;
    }
}
