package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.ChangePasswordCommand;
import com.neighbourly.userservice.dto.ChangePasswordRequestDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.exception.InvalidCredentialsException;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.util.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordCommandHandler implements CommandHandler<ChangePasswordCommand, Void> {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    public ChangePasswordCommandHandler(UserRepository userRepository,
                                        PasswordEncoder passwordEncoder,
                                        PasswordValidator passwordValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordValidator = passwordValidator;
    }

    @Override
    public Either<String, Void> handle(ChangePasswordCommand command) {
        try {
            ChangePasswordRequestDTO dto = command.getChangePasswordRequestDTO();

            // Validate new password
            String passwordError = passwordValidator.validatePassword(dto.getNewPassword());
            if (passwordError != null) {
                return Either.left(passwordError);
            }

            // Check if new password is same as current
            if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
                return Either.left("New password must be different from current password");
            }

            User user = userRepository.findById(command.getUserId())
                    .orElseThrow(() -> new InvalidCredentialsException("User not found"));

            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                return Either.left("Current password is incorrect");
            }

            // Check if new password matches any of last N passwords (if you store password history)

            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            return Either.right(null);
        } catch (InvalidCredentialsException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to change password: " + e.getMessage());
        }
    }
}