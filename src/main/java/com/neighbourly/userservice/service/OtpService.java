package com.neighbourly.userservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();
    private final JavaMailSender mailSender;
    private final int otpLength = 6;
    private final int otpValidityMinutes = 5;

    public OtpService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static class OtpEntry {
        String otp;
        LocalDateTime expiryTime;

        OtpEntry(String otp, LocalDateTime expiryTime) {
            this.otp = otp;
            this.expiryTime = expiryTime;
        }
    }

    public String generateAndSendOtp(String identifier) {
        String otp = generateRandomOtp();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(otpValidityMinutes);
        otpStore.put(identifier, new OtpEntry(otp, expiry));
        logger.info("Generated OTP for {}: {} (valid until {})", identifier, otp, expiry);
        sendOtp(identifier, otp);
        return otp;
    }

    public boolean verifyOtp(String identifier, String otp) {
        OtpEntry entry = otpStore.get(identifier);
        if (entry == null) {
            logger.warn("OTP verification failed for {}: no entry found", identifier);
            return false;
        }
        if (LocalDateTime.now().isAfter(entry.expiryTime)) {
            logger.warn("OTP expired for {}", identifier);
            otpStore.remove(identifier);
            return false;
        }
        boolean isValid = entry.otp.equals(otp);
        if (isValid) {
            logger.info("OTP verification succeeded for {}", identifier);
            otpStore.remove(identifier);
        } else {
            logger.warn("OTP verification failed for {}: incorrect OTP", identifier);
        }
        return isValid;
    }

    private String generateRandomOtp() {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void sendOtp(String identifier, String otp) {
        if (identifier.contains("@")) {
            sendEmailOtp(identifier, otp);
        } else {
            sendSmsOtp(identifier, otp);
        }
    }

    private void sendEmailOtp(String email, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your OTP Code");
            helper.setText("Your OTP is: " + otp);
            mailSender.send(message);
            logger.info("OTP email sent to {}", email);
            logger.info("OTP  {}", otp);

        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    private void sendSmsOtp(String phoneNumber, String otp) {
        // Placeholder for SMS sending
        logger.info("Sending SMS OTP {} to {}", otp, phoneNumber);
        // Integration with actual SMS API would go here
    }
}
