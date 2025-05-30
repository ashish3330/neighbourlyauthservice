package com.neighbourly.userservice.service;

import com.neighbourly.commonservice.dispatcher.Dispatcher;
import com.neighbourly.commonservice.dispatcher.SyncDispatcher;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.commonservice.service.GenericService;
import com.neighbourly.userservice.command.CreateServiceProviderProfileCommand;
import com.neighbourly.userservice.command.SubmitDocumentCommand;
import com.neighbourly.userservice.command.SubmitRatingCommand;
import com.neighbourly.userservice.command.VerifyDocumentCommand;
import com.neighbourly.userservice.dto.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService implements GenericService<ServiceProviderProfileDTO, Long, ServiceProviderProfileDTO> {

    private final SyncDispatcher dispatcher;

    public UserProfileService(@Qualifier("userProfileSyncDispatcher") SyncDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    public Either<String, ServiceProviderProfileDTO> createServiceProviderProfile(CreateServiceProviderProfileRequestDTO requestDTO) {
        return dispatcher.dispatch(new CreateServiceProviderProfileCommand(requestDTO));
    }

    public Either<String, ServiceProviderProfileDTO> getServiceProviderProfile(Long userId, Long serviceId) {
        return Either.left("Not implemented"); // Implement query handler if needed
    }

    public Either<String, DocumentDTO> submitDocument(SubmitDocumentRequestDTO requestDTO) {
        return dispatcher.dispatch(new SubmitDocumentCommand(requestDTO));
    }

    public Either<String, DocumentDTO> verifyDocument(VerifyDocumentRequestDTO requestDTO) {
        return dispatcher.dispatch(new VerifyDocumentCommand(requestDTO));
    }

    public Either<String, RatingDTO> submitRating(SubmitRatingRequestDTO requestDTO) {
        return dispatcher.dispatch(new SubmitRatingCommand(requestDTO));
    }

    public Either<String, List<RatingDTO>> getRatingsForProvider(Long profileId) {
        return Either.left("Not implemented"); // Implement query handler if needed
    }

    @Override
    public Either<String, ServiceProviderProfileDTO> create(ServiceProviderProfileDTO dto) {
        CreateServiceProviderProfileRequestDTO requestDTO = new CreateServiceProviderProfileRequestDTO(
                dto.userId(),
                dto.serviceId()
        );
        return createServiceProviderProfile(requestDTO);
    }

    @Override
    public Either<String, Optional<ServiceProviderProfileDTO>> getById(Long id) {
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, List<ServiceProviderProfileDTO>> getAll() {
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, ServiceProviderProfileDTO> update(Long id, ServiceProviderProfileDTO dto) {
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, Void> delete(Long id) {
        return Either.left("Not implemented");
    }
}
