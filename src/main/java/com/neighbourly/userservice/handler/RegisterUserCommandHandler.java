package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RegisterUserCommand;
import com.neighbourly.userservice.dto.RegisterRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.OtpService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, UserDTO> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    public RegisterUserCommandHandler(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder, OtpService otpService) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
    }

    @Override
    public Either<String, UserDTO> handle(RegisterUserCommand command) {
        try {
            RegisterRequestDTO dto = command.getRegisterRequestDTO();



            // Check if user already exists
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                return Either.left("User with this email already exists");
            }
            // Verify OTP matches the email

            if (!otpService.verifyOtp(dto.getEmail(), dto.getOtp())) {
                return Either.left("Invalid or expired OTP for email: " + dto.getEmail());
            }

            // Create new user
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setLatitude(null);
            user.setLongitude(null);
            user.setRoles(new HashMap<>());
            User savedUser = userRepository.save(user);
            UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);
            return Either.right(userDTO);
        } catch (Exception e) {
            return Either.left("Failed to register user: " + e.getMessage());
        }
    }
}