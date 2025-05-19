package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RegisterUserCommand;
import com.neighbourly.userservice.dto.RegisterRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.OtpService;
import com.neighbourly.userservice.util.PasswordValidator;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, UserDTO> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public RegisterUserCommandHandler(UserRepository userRepository, ModelMapper modelMapper,
                                      OtpService otpService, PasswordEncoder passwordEncoder, PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public Either<String, UserDTO> handle(RegisterUserCommand command) {
        try {
            RegisterRequestDTO dto = command.getRegisterRequestDTO();
            if (dto.getEmail() == null || dto.getOtp() == null) {
                return Either.left("Email and OTP are required");
            }

            // Verify OTP
            if (!otpService.verifyOtp(dto.getEmail(), dto.getOtp())) {
                return Either.left("Invalid OTP");
            }

            // Check for existing email or phone number
            if (userRepository.existsByEmail(dto.getEmail())) {
                return Either.left("Email already exists");
            }
            if (dto.getPhoneNumber() != null && userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
                return Either.left("Phone number already exists");
            }

            // Enforce password to be mandatory
            if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
                return Either.left("Password is required");
            }

            String passwordError = passwordValidator.validatePassword(dto.getPassword());
            if (passwordError != null) {
                return Either.left(passwordError);
            }

            // Map DTO to entity
            User user = modelMapper.map(dto, User.class);
            user.setPassword(passwordEncoder.encode(dto.getPassword()));

            User savedUser = userRepository.save(user);
            UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);
            return Either.right(userDTO);
        } catch (Exception e) {
            return Either.left("Failed to register user: " + e.getMessage());
        }
    }
}