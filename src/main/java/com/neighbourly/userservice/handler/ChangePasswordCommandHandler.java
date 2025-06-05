package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.ChangePasswordCommand;
import com.neighbourly.userservice.dto.ChangePasswordRequestDTO;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.exception.InvalidCredentialsException;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.util.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordCommandHandler implements CommandHandler<ChangePasswordCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(ChangePasswordCommandHandler.class);

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
        ChangePasswordRequestDTO dto = command.getChangePasswordRequestDTO();
        Long userId = command.getUserId();
        log.info("Initiating password change for userId: {}", userId);

        try {
            // Validate new password
            String passwordError = passwordValidator.validatePassword(dto.getNewPassword());
            if (passwordError != null) {
                log.warn("Password validation failed for userId: {}: {}", userId, passwordError);
                return Either.left(passwordError);
            }

            // Check if new password is same as current
            if (dto.getCurrentPassword().equals(dto.getNewPassword())) {
                log.warn("New password is same as current for userId: {}", userId);
                return Either.left("New password must be different from current password");
            }

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new InvalidCredentialsException("User not found"));

            if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
                log.warn("Incorrect current password for userId: {}", userId);
                return Either.left("Current password is incorrect");
            }

            // Update password
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);

            log.info("Password changed successfully for userId: {}", userId);
            return Either.right(null);

        } catch (InvalidCredentialsException e) {
            log.error("Invalid credentials while changing password for userId: {}: {}", userId, e.getMessage());
            return Either.left(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while changing password for userId: {}", userId, e);
            return Either.left("Failed to change password: " + e.getMessage());
        }
    }
}
