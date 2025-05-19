package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SetPasswordCommand;
import com.neighbourly.userservice.dto.SetPasswordRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.util.PasswordValidator;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SetPasswordCommandHandler implements CommandHandler<SetPasswordCommand, UserDTO> {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public SetPasswordCommandHandler(UserRepository userRepository,
                                     ModelMapper modelMapper,
                                     PasswordEncoder passwordEncoder,
                                     PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public Either<String, UserDTO> handle(SetPasswordCommand command) {
        try {
            SetPasswordRequestDTO dto = command.getSetPasswordRequestDTO();

            // Validate password
            String passwordError = passwordValidator.validatePassword(dto.getPassword());
            if (passwordError != null) {
                return Either.left(passwordError);
            }

            // Find user by email
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Update password (allow reset even if password exists)
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            User savedUser = userRepository.save(user);
            UserDTO userDTO = modelMapper.map(savedUser, UserDTO.class);
            return Either.right(userDTO);
        } catch (IllegalArgumentException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to reset password: " + e.getMessage());
        }
    }
}