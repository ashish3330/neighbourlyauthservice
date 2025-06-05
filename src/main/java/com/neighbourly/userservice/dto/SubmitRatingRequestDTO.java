package com.neighbourly.userservice.dto;

import java.math.BigDecimal;

public record SubmitRatingRequestDTO(
        Long serviceProviderProfileId,
        Long raterUserId,
        Long serviceId,
        BigDecimal rating,
        String comment
) {}