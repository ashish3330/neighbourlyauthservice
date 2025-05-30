package com.neighbourly.userservice.dto;

public record CreateServiceProviderProfileRequestDTO(
        Long userId,
        Long serviceId
) {}