package com.neighbourly.userservice.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

public record ServiceProviderProfileDTO(
        Long id,
        Long userId,
        Long serviceId,
        String serviceName,
        String verificationStatus,
        BigDecimal averageRating,
        Integer ratingCount,
        Instant createdAt,
        Instant updatedAt
) {}
