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
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
            User user = userRepository.findByEmail(userInfo.getEmail())
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(userInfo.getEmail());
                        newUser.setName(userInfo.getName());
                        newUser.setPhoneNumber("");
                        newUser.setPassword("");
                        newUser.setRoles(Map.of("user", List.of("read"))); // Default role
                        return userRepository.save(newUser);
                    });

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
            String refreshToken = jwtService.generateRefreshToken(user.getId());

            return Either.right(new LoginResponseDTO(token, refreshToken, userDTO, null, null, null));
        } catch (Exception e) {
            return Either.left("Google SSO login failed: " + e.getMessage());
        }
    }
}