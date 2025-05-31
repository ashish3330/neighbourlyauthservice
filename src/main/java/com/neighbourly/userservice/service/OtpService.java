package com.neighbourly.userservice.service;

import com.neighbourly.userservice.entity.Otp;
import com.neighbourly.userservice.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final JavaMailSender javaMailSender;
    private static final int OTP_VALIDITY_MINUTES = 3;

    public OtpService(OtpRepository otpRepository, JavaMailSender javaMailSender) {
        this.otpRepository = otpRepository;
        this.javaMailSender = javaMailSender;
    }

    @Transactional
    public String generateAndSendOtp(String identifier) {
        if (!isValidEmail(identifier)) {
            throw new IllegalArgumentException("Invalid email format");
        }

        try {
            String otp = generateOtp();
            identifier = identifier.trim().toLowerCase(); // Normalize identifier

            // Check if an OTP exists for the identifier
            Optional<Otp> existingOtp = otpRepository.findByIdentifier(identifier);
            Otp otpEntity;
            if (existingOtp.isPresent()) {
                // Update existing OTP
                otpEntity = existingOtp.get();
                otpEntity.setOtp(otp);
                otpEntity.setCreatedAt(LocalDateTime.now());
                otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
            } else {
                // Create new OTP
                otpEntity = new Otp();
                otpEntity.setIdentifier(identifier);
                otpEntity.setOtp(otp);
                otpEntity.setCreatedAt(LocalDateTime.now());
                otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));
            }

            // Save (insert or update) the OTP entity
            otpRepository.saveAndFlush(otpEntity);

            // Send OTP via email
            sendOtp(identifier, otp);
            return otp;
        } catch (Exception e) {
            // Use a proper logger (e.g., SLF4J) in production
            System.err.println("Error generating/sending OTP: " + e.getMessage());
            throw new RuntimeException("Failed to generate or send OTP", e);
        }
    }

    @Transactional
    public boolean verifyOtp(String identifier, String otp) {
        identifier = identifier.trim().toLowerCase(); // Normalize identifier
        Optional<Otp> otpEntity = otpRepository.findByIdentifierAndOtp(identifier, otp);
        if (otpEntity.isEmpty()) {
            return false; // OTP not found or incorrect
        }
        Otp foundOtp = otpEntity.get();
        if (LocalDateTime.now().isAfter(foundOtp.getExpiresAt())) {
            return false; // OTP expired
        }
        // OTP is valid, clean up
        otpRepository.deleteByIdentifier(identifier);
        return true;
    }

    @Transactional
    @Scheduled(fixedRate = 60000) // Run every 60 seconds
    public void deleteExpiredOtps() {
        try {
            otpRepository.deleteByExpiresAtBefore(LocalDateTime.now());
        } catch (Exception e) {
            // Use a proper logger (e.g., SLF4J) in production
            System.err.println("Error deleting expired OTPs: " + e.getMessage());
        }
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void sendOtp(String identifier, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(identifier);
            message.setSubject("Your OTP for Registration");
            System.out.println("Sending OTP to: " + identifier);
            System.out.println("OTP: " + otp);
            message.setText("Your OTP is: " + otp + ". It is valid for " + OTP_VALIDITY_MINUTES + " minutes.");
            message.setFrom("your-email@gmail.com"); // Must match spring.mail.username
            javaMailSender.send(message);
        } catch (Exception e) {
            // Use a proper logger (e.g., SLF4J) in production
            System.err.println("Error sending OTP email: " + e.getMessage());
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    private boolean isValidEmail(String identifier) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return identifier != null && identifier.matches(emailRegex);
    }
}