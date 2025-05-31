package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.GoogleSsoLoginCommand;
import com.neighbourly.userservice.dto.LoginResponseDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.GoogleSsoService;
import com.neighbourly.userservice.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GoogleSsoLoginCommandHandler implements CommandHandler<GoogleSsoLoginCommand, LoginResponseDTO> {

    private final GoogleSsoService googleSsoService;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtService jwtService;

    public GoogleSsoLoginCommandHandler(GoogleSsoService googleSsoService, UserRepository userRepository,
                                        ModelMapper modelMapper, JwtService jwtService) {
        this.googleSsoService = googleSsoService;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
    }

    @Override
    public Either<String, LoginResponseDTO> handle(GoogleSsoLoginCommand command) {
        try {
            if (command.getGoogleIdToken() == null || command.getGoogleIdToken().isEmpty()) {
                return Either.left("Google ID token is required");
            }
            GoogleSsoService.GoogleUserInfo userInfo = googleSsoService.verifyIdToken(command.getGoogleIdToken());
            User user = userRepository.findByGoogleId(userInfo.getGoogleId())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setGoogleId(userInfo.getGoogleId());
                        newUser.setEmail(userInfo.getEmail());
                        newUser.setName(userInfo.getName());
                        newUser.setPhoneNumber("");
                        newUser.setPassword("");
                        return userRepository.save(newUser);
                    });


            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            String token = jwtService.generateToken(user.getId(),user.getEmail(),user.getRoles());
            String refreshToken = jwtService.generateRefreshToken(user.getId());

            Cookie jwtCookie = new Cookie("jwtToken", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(7 * 24 * 60 * 60);
            jwtCookie.setAttribute("SameSite", "Strict");

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(30 * 24 * 60 * 60);
            refreshCookie.setAttribute("SameSite", "Strict");
            return Either.right(new LoginResponseDTO(token, userDTO, jwtCookie, refreshCookie, null));
        } catch (Exception e) {
            return Either.left("Google SSO login failed: " + e.getMessage());
        }
    }
}