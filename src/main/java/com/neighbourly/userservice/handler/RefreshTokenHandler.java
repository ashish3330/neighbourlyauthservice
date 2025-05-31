package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RefreshTokenCommand;
import com.neighbourly.userservice.dto.LoginResponseDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenHandler implements CommandHandler<RefreshTokenCommand, LoginResponseDTO> {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public RefreshTokenHandler(JwtService jwtService, UserRepository userRepository, ModelMapper modelMapper) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
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

            Cookie jwtCookie = new Cookie("jwtToken", newAccessToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60);
            jwtCookie.setAttribute("SameSite", "Strict");

            return Either.right(new LoginResponseDTO(newAccessToken, userDTO, jwtCookie, null, null));
        } catch (Exception e) {
            return Either.left("Refresh failed: " + e.getMessage());
        }
    }
}