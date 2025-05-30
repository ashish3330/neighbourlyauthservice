package com.neighbourly.userservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RatingDTO(
        Long id,
        Long serviceProviderProfileId,
        Long raterUserId,
        Long serviceId,
        BigDecimal rating,
        String comment,
        LocalDateTime createdAt
) {}