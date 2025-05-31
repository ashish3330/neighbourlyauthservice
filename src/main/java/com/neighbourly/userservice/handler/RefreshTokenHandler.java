package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RefreshTokenCommand;
import com.neighbourly.userservice.dto.LoginResponseDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.JwtService;
import com.neighbourly.userservice.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenHandler implements CommandHandler<RefreshTokenCommand, LoginResponseDTO> {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenHandler.class);

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final CookieUtil cookieUtil;

    public RefreshTokenHandler(JwtService jwtService, UserRepository userRepository,
                               ModelMapper modelMapper, CookieUtil cookieUtil) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public Either<String, LoginResponseDTO> handle(RefreshTokenCommand command) {
        try {
            String refreshToken = command.getRefreshToken();
            logger.info("Handling refresh token request");

            if (!jwtService.validateRefreshToken(refreshToken)) {
                logger.warn("Invalid or expired refresh token: {}", refreshToken);
                return Either.left("Invalid or expired refresh token");
            }

            Long userId = jwtService.getUserIdFromToken(refreshToken);
            logger.debug("Extracted userId from refresh token: {}", userId);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        logger.error("User not found with id: {}", userId);
                        return new RuntimeException("User not found");
                    });

            String newAccessToken = jwtService.generateToken(userId, user.getEmail(), user.getRoles());
            logger.info("Generated new access token for userId: {}", userId);

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            Cookie jwtCookie = cookieUtil.createServletAccessCookie(newAccessToken);

            logger.info("Refresh token process successful for userId: {}", userId);
            return Either.right(new LoginResponseDTO(newAccessToken, refreshToken, userDTO, jwtCookie, null, null));

        } catch (Exception e) {
            logger.error("Refresh token process failed", e);
            return Either.left("Refresh failed: " + e.getMessage());
        }
    }
}
