package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SubmitDocumentCommand;
import com.neighbourly.userservice.dto.DocumentDTO;
import com.neighbourly.userservice.dto.SubmitDocumentRequestDTO;
import com.neighbourly.userservice.entity.DocumentEntity;
import com.neighbourly.userservice.entity.ServiceEntity;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.exception.InvalidInputException;
import com.neighbourly.userservice.repository.DocumentRepository;
import com.neighbourly.userservice.repository.ServiceRepository;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.util.CommonValidationUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubmitDocumentCommandHandler implements CommandHandler<SubmitDocumentCommand, DocumentDTO> {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final DocumentRepository documentRepository;
    private final ModelMapper modelMapper;
    private final CommonValidationUtil validationUtil;

    public SubmitDocumentCommandHandler(
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            DocumentRepository documentRepository,
            ModelMapper modelMapper,
            CommonValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.documentRepository = documentRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    @Transactional
    public Either<String, DocumentDTO> handle(SubmitDocumentCommand command) {
        try {
            SubmitDocumentRequestDTO dto = command.getRequestDTO();
            validationUtil.validateNotNull(dto.userId(), "User ID");
            validationUtil.validateNotNull(dto.serviceId(), "Service ID");
            validationUtil.validateNotNull(dto.documentType(), "Document type");
            validationUtil.validateNotNull(dto.filePath(), "File path");

            User user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new InvalidInputException("User not found with id: " + dto.userId()));
            ServiceEntity service = serviceRepository.findById(dto.serviceId())
                    .orElseThrow(() -> new InvalidInputException("Service not found with id: " + dto.serviceId()));

            validateDocumentType(dto.documentType(), service.getName());

            DocumentEntity document = new DocumentEntity();
            document.setUser(user);
            document.setService(service);
            document.setDocumentType(dto.documentType());
            document.setFilePath(dto.filePath());
            DocumentEntity savedDocument = documentRepository.save(document);

            return Either.right(mapToDTO(savedDocument));
        } catch (InvalidInputException e) {
            return Either.left(e.getMessage());
        } catch (Exception e) {
            return Either.left("Failed to submit document: " + e.getMessage());
        }
    }

    private void validateDocumentType(String documentType, String serviceName) {
        List<String> validTypes = switch (serviceName) {
            case "FOOD_DELIVERY" -> List.of("HEALTH_LICENSE", "FOOD_SAFETY_CERT");
            case "CAB_RENTING" -> List.of("DRIVERS_LICENSE", "VEHICLE_REGISTRATION");
            case "BIKE_RENTAL" -> List.of("BUSINESS_LICENSE", "INSURANCE");
            case "LOCAL_BUSINESS_DELIVERY" -> List.of("BUSINESS_LICENSE", "TAX_ID");
            case "DELIVERY_SHARING" -> List.of("BUSINESS_LICENSE", "LOGISTICS_PERMIT");
            case "NEXTDOOR" -> List.of("ID_PROOF", "ADDRESS_PROOF");
            default -> throw new InvalidInputException("Unknown service: " + serviceName);
        };
        if (!validTypes.contains(documentType)) {
            throw new InvalidInputException("Invalid document type for service " + serviceName + ": " + documentType);
        }
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