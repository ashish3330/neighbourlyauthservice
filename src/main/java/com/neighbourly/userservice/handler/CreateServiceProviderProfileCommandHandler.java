package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.CreateServiceProviderProfileCommand;
import com.neighbourly.userservice.dto.CreateServiceProviderProfileRequestDTO;
import com.neighbourly.userservice.dto.ServiceProviderProfileDTO;
import com.neighbourly.userservice.entity.ServiceEntity;
import com.neighbourly.userservice.entity.ServiceProviderProfileEntity;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.exception.InvalidInputException;
import com.neighbourly.userservice.repository.ServiceProviderProfileRepository;
import com.neighbourly.userservice.repository.ServiceRepository;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.util.CommonValidationUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CreateServiceProviderProfileCommandHandler implements CommandHandler<CreateServiceProviderProfileCommand, ServiceProviderProfileDTO> {

    private static final Logger log = LoggerFactory.getLogger(CreateServiceProviderProfileCommandHandler.class);

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceProviderProfileRepository profileRepository;
    private final ModelMapper modelMapper;
    private final CommonValidationUtil validationUtil;

    public CreateServiceProviderProfileCommandHandler(
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            ServiceProviderProfileRepository profileRepository,
            ModelMapper modelMapper,
            CommonValidationUtil validationUtil) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.profileRepository = profileRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    @Transactional
    public Either<String, ServiceProviderProfileDTO> handle(CreateServiceProviderProfileCommand command) {
        CreateServiceProviderProfileRequestDTO dto = command.getRequestDTO();
        log.info("Creating service provider profile for userId: {}, serviceId: {}", dto.userId(), dto.serviceId());

        try {
            validationUtil.validateNotNull(dto.userId(), "User ID");
            validationUtil.validateNotNull(dto.serviceId(), "Service ID");

            User user = userRepository.findById(dto.userId())
                    .orElseThrow(() -> new InvalidInputException("User not found with id: " + dto.userId()));
            ServiceEntity service = serviceRepository.findById(dto.serviceId())
                    .orElseThrow(() -> new InvalidInputException("Service not found with id: " + dto.serviceId()));

            if (profileRepository.findByUserIdAndServiceId(dto.userId(), dto.serviceId()).isPresent()) {
                log.warn("Profile already exists for userId: {}, serviceId: {}", dto.userId(), dto.serviceId());
                return Either.left("Profile already exists for this user and service");
            }

            // Assign role
            String role = "ROLE_" + service.getName() + "_PROVIDER";
            Map<String, List<String>> roles = user.getRoles();
            roles.computeIfAbsent(service.getName(), k -> new ArrayList<>()).add(role);
            user.setRoles(roles);
            userRepository.save(user);
            log.info("Assigned role '{}' to userId: {}", role, dto.userId());

            ServiceProviderProfileEntity profile = new ServiceProviderProfileEntity();
            profile.setUser(user);
            profile.setService(service);
            profile.setVerificationStatus("PENDING");
            ServiceProviderProfileEntity savedProfile = profileRepository.save(profile);

            log.info("Successfully created service provider profile with id: {}", savedProfile.getId());
            return Either.right(mapToDTO(savedProfile));

        } catch (InvalidInputException e) {
            log.error("Validation failed while creating profile: {}", e.getMessage());
            return Either.left(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating service provider profile for userId: {}, serviceId: {}: {}",
                    dto.userId(), dto.serviceId(), e.getMessage(), e);
            return Either.left("Failed to create service provider profile: " + e.getMessage());
        }
    }

    private ServiceProviderProfileDTO mapToDTO(ServiceProviderProfileEntity entity) {
        return  modelMapper.map(entity, ServiceProviderProfileDTO.class);
    }
}
