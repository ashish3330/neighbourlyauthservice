package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.LoginUserCommand;
import com.neighbourly.userservice.dto.LoginRequestDTO;
import com.neighbourly.userservice.dto.LoginResponseDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.exception.InvalidCredentialsException;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.JwtService;
import com.neighbourly.userservice.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class LoginUserCommandHandler implements CommandHandler<LoginUserCommand, LoginResponseDTO> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private  final CookieUtil  cookieUtil;

    public LoginUserCommandHandler(UserRepository userRepository, ModelMapper modelMapper,
                                   PasswordEncoder passwordEncoder, JwtService jwtService, CookieUtil cookieUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public Either<String, LoginResponseDTO> handle(LoginUserCommand command) {
        try {
            LoginRequestDTO dto = command.getLoginRequestDTO();
            if (dto.getIdentifier() == null || dto.getPassword() == null) {
                return Either.left("Identifier and password are required");
            }
            User user = userRepository.findByEmail(dto.getIdentifier())
                    .or(() -> userRepository.findByPhoneNumber(dto.getIdentifier()))
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email/phone number or password"));

            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                return Either.left("Account requires password setup");
            }

            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                throw new InvalidCredentialsException("Invalid email/phone number or password");
            }

            UserDTO userDTO = modelMapper.map(user, UserDTO.class);
            String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRoles());
            String refreshToken = jwtService.generateRefreshToken(user.getId());

            Cookie jwtCookie = cookieUtil.createServletAccessCookie(token);
            Cookie refreshCookie = cookieUtil.createServletRefreshCookie(refreshToken);
            return Either.right(new LoginResponseDTO(token, refreshToken, userDTO, jwtCookie, refreshCookie, null));
        } catch (InvalidCredentialsException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Login failed: " + e.getMessage());
        }
    }
}