package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.RequestOtpCommand;
import com.neighbourly.userservice.dto.RequestOtpDTO;
import com.neighbourly.userservice.service.OtpService;
import org.springframework.stereotype.Component;

@Component
public class RequestOtpCommandHandler implements CommandHandler<RequestOtpCommand, String> {

    private final OtpService otpService;

    public RequestOtpCommandHandler(OtpService otpService) {
        this.otpService = otpService;
    }

    @Override
    public Either<String, String> handle(RequestOtpCommand command) {
        try {
            RequestOtpDTO dto = command.getRequestOtpDTO();
            String identifier = dto.getIdentifier();
            if (identifier == null || identifier.isEmpty()) {
                return Either.left("Identifier is required");
            }
            String otp = otpService.generateAndSendOtp(identifier);
            return Either.right("OTP sent to " + identifier);
        } catch (Exception e) {
            return Either.left("Failed to send OTP: " + e.getMessage());
        }
    }
}