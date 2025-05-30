package com.neighbourly.userservice.handler;


import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.VerifyDocumentCommand;
import com.neighbourly.userservice.dto.DocumentDTO;
import com.neighbourly.userservice.dto.VerifyDocumentRequestDTO;
import com.neighbourly.userservice.entity.DocumentEntity;
import com.neighbourly.userservice.entity.ServiceProviderProfileEntity;
import com.neighbourly.userservice.exception.InvalidInputException;
import com.neighbourly.userservice.repository.DocumentRepository;
import com.neighbourly.userservice.repository.ServiceProviderProfileRepository;
import com.neighbourly.userservice.util.CommonValidationUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class VerifyDocumentCommandHandler implements CommandHandler<VerifyDocumentCommand, DocumentDTO> {

    private final DocumentRepository documentRepository;
    private final ServiceProviderProfileRepository profileRepository;
    private final ModelMapper modelMapper;
    private final CommonValidationUtil validationUtil;

    public VerifyDocumentCommandHandler(
            DocumentRepository documentRepository,
            ServiceProviderProfileRepository profileRepository,
            ModelMapper modelMapper,
            CommonValidationUtil validationUtil) {
        this.documentRepository = documentRepository;
        this.profileRepository = profileRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    @Transactional
    public Either<String, DocumentDTO> handle(VerifyDocumentCommand command) {
        try {
            VerifyDocumentRequestDTO dto = command.getRequestDTO();
            validationUtil.validateNotNull(dto.documentId(), "Document ID");
            validationUtil.validateNotNull(dto.status(), "Status");

            if (!List.of("VERIFIED", "REJECTED").contains(dto.status())) {
                return Either.left("Invalid status: " + dto.status());
            }

            DocumentEntity document = documentRepository.findById(dto.documentId())
                    .orElseThrow(() -> new InvalidInputException("Document not found with id: " + dto.documentId()));

            document.setStatus(dto.status());
            if ("VERIFIED".equals(dto.status())) {
                document.setVerifiedAt(Instant.now());
            }
            DocumentEntity savedDocument = documentRepository.save(document);

            updateProfileVerificationStatus(document.getUser().getId(), document.getService().getId());

            return Either.right(mapToDTO(savedDocument));
        } catch (InvalidInputException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to verify document: " + e.getMessage());
        }
    }

    private void updateProfileVerificationStatus(Long userId, Long serviceId) {
        ServiceProviderProfileEntity profile = profileRepository.findByUserIdAndServiceId(userId, serviceId)
                .orElseThrow(() -> new InvalidInputException("Profile not found"));
        List<DocumentEntity> documents = documentRepository.findByUserIdAndServiceId(userId, serviceId);
        boolean allVerified = documents.stream().allMatch(doc -> "VERIFIED".equals(doc.getStatus()));
        profile.setVerificationStatus(allVerified ? "VERIFIED" : "PENDING");
        profile.setUpdatedAt(Instant.now());
        profileRepository.save(profile);
    }

    private DocumentDTO mapToDTO(DocumentEntity entity) {
        return new DocumentDTO(
                entity.getId(),
                entity.getUser().getId(),
                entity.getService().getId(),
                entity.getDocumentType(),
                entity.getStatus(),
                entity.getUploadedAt(),
                entity.getVerifiedAt()
        );
    }
}