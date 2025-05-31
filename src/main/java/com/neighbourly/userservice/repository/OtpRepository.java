package com.neighbourly.userservice.repository;

import com.neighbourly.userservice.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findByIdentifierAndOtp(String identifier, String otp);
    Optional<Otp> findByIdentifier(String identifier);
    void deleteByIdentifier(String identifier);
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);
}