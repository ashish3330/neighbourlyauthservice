package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RefreshTokenCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.neighbourly.userservice.service.TokenService;

import org.springframework.stereotype.Component;
@Component
public class RefreshTokenCommandHandler implements CommandHandler<RefreshTokenCommand, String> {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCommandHandler.class);
    private final TokenService tokenService;

    public RefreshTokenCommandHandler(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Either<String, String> handle(RefreshTokenCommand command) {
        try {
            if (command.getToken() == null || command.getToken().isEmpty()) {
                logger.warn("Token is required for refresh");
                return Either.left("Token is required");
            }
            String newToken = tokenService.refreshToken(command.getToken());
            logger.info("Successfully refreshed token");
            return Either.right(newToken);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid token: {}", e.getMessage());
            return Either.left("Invalid or expired token: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            return Either.left("Token refresh failed: " + e.getMessage());
        }
    }
}