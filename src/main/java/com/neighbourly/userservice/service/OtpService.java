package com.neighbourly.userservice.service;

import com.neighbourly.userservice.entity.Otp;
import com.neighbourly.userservice.repository.OtpRepository;
import jakarta.transaction.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {
    private final OtpRepository otpRepository;
    private final JavaMailSender javaMailSender;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_VALIDITY_MINUTES = 5;

    public OtpService(OtpRepository otpRepository, JavaMailSender javaMailSender) {
        this.otpRepository = otpRepository;
        this.javaMailSender = javaMailSender;
    }


    @Transactional
    public String generateAndSendOtp(String identifier) {
        // Generate a 6-digit OTP
        String otp = generateOtp();

        // Store OTP in PostgreSQL
        Otp otpEntity = new Otp();
        otpEntity.setIdentifier(identifier);
        otpEntity.setOtp(otp);
        otpEntity.setCreatedAt(LocalDateTime.now());
        otpEntity.setExpiresAt(LocalDateTime.now().plusMinutes(OTP_VALIDITY_MINUTES));

        // Clean up old OTPs for this identifier
        otpRepository.deleteByIdentifier(identifier);
        otpRepository.save(otpEntity);

        // Send OTP via email
        sendOtp(identifier, otp);

        return otp;
    }
    @Transactional
    public boolean verifyOtp(String identifier, String otp) {
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
    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000); // 6-digit OTP
        return String.valueOf(otp);
    }

    private void sendOtp(String identifier, String otp) {
        // Implement email sending logic using JavaMailSender
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(identifier);
        System.out.println("Sending OTP: " + otp);
        message.setSubject("Your OTP for Registration");
        message.setText("Your OTP is: " + otp + ". It is valid for " + OTP_VALIDITY_MINUTES + " minutes.");
        message.setFrom("your-email@gmail.com"); // Must match spring.mail.username
        javaMailSender.send(message);
    }
}