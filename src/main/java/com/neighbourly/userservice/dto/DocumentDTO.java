package com.neighbourly.userservice.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record DocumentDTO(
        Long id,
        Long userId,
        Long serviceId,
        String documentType,
        String status,
        Instant uploadedAt,
        Instant verifiedAt
) {}