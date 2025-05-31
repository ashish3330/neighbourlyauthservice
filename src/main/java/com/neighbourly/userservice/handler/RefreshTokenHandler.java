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
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenHandler implements CommandHandler<RefreshTokenCommand, LoginResponseDTO> {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final CookieUtil cookieUtil;

    public RefreshTokenHandler(JwtService jwtService, UserRepository userRepository, ModelMapper modelMapper, CookieUtil cookieUtil) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public Either<String, LoginResponseDTO> handle(RefreshTokenCommand command) {
        try {
            String refreshToken = command.getRefreshToken();
            if (!jwtService.validateRefreshToken(refreshToken)) {
                return Either.left("Invalid or expired refresh token");
            }
            Long userId = jwtService.getUserIdFromToken(refreshToken);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            String newAccessToken = jwtService.generateToken(userId, user.getEmail(), user.getRoles());
            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            Cookie jwtCookie = cookieUtil.createServletAccessCookie(newAccessToken);
            return Either.right(new LoginResponseDTO(newAccessToken, refreshToken, userDTO, jwtCookie, null, null));
        } catch (Exception e) {
            return Either.left("Refresh failed: " + e.getMessage());
        }
    }
}