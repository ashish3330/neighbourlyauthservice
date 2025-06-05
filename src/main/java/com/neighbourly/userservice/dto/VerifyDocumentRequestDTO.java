package com.neighbourly.userservice.dto;

public record VerifyDocumentRequestDTO(
        Long documentId,
        String status
) {}