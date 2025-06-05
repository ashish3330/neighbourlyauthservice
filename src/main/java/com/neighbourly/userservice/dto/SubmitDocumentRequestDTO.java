package com.neighbourly.userservice.dto;

public record SubmitDocumentRequestDTO(
        Long userId,
        Long serviceId,
        String documentType,
        String filePath // Path after upload to secure storage
) {}
