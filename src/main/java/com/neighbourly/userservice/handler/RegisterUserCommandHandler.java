package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RegisterUserCommand;
import com.neighbourly.userservice.dto.RegisterRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand, UserDTO> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserCommandHandler(UserRepository userRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Either<String, UserDTO> handle(RegisterUserCommand command) {
        try {
            RegisterRequestDTO dto = command.getRegisterRequestDTO();

            // Check if user already exists
            if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
                return Either.left("User with this email already exists");
            }

            // Create new user
            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPhoneNumber(dto.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setLatitude(null); // Explicitly set to null
            user.setLongitude(null);
            user.setRoles(List.of("USER"));
            ;// Explicitly set to null
            // Set other fields like roles, createdAt, etc., as needed

            User savedUser = userRepository.save(user);
            UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);
            return Either.right(userDTO);
        } catch (Exception e) {
            return Either.left("Failed to register user: " + e.getMessage());
        }
    }
}