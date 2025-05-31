package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SetLocationCommand;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.Address;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.AddressRepository;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.GeocodeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SetLocationCommandHandler implements CommandHandler<SetLocationCommand, UserDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SetLocationCommandHandler.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final GeocodeService geocodeService;

    public SetLocationCommandHandler(UserRepository userRepository, AddressRepository addressRepository,
                                     ModelMapper modelMapper, GeocodeService geocodeService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.geocodeService = geocodeService;
    }

    @Override
    public Either<String, UserDTO> handle(SetLocationCommand command) {
        SetLocationRequestDTO dto = command.getSetLocationRequestDTO();
        logger.info("SetLocationCommandHandler - setting location for email: {}", dto.getEmail());

        if (dto.getLatitude() == null || dto.getLongitude() == null) {
            logger.warn("SetLocationCommandHandler - latitude or longitude is null for email: {}", dto.getEmail());
            return Either.left("Latitude and longitude must not be null");
        }

        try {
            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("SetLocationCommandHandler - user not found for email: {}", dto.getEmail());
                        return new IllegalArgumentException("User not found");
                    });

            Address address = geocodeService.reverseGeocode(dto.getLatitude(), dto.getLongitude());

            user.setLatitude(dto.getLatitude());
            user.setLongitude(dto.getLongitude());
            user.setAddress(address);
            address.setUser(user);

            addressRepository.save(address);
            User savedUser = userRepository.save(user);

            logger.info("SetLocationCommandHandler - location set successfully for email: {}", dto.getEmail());
            return Either.right(modelMapper.map(savedUser, UserDTO.class));
        } catch (Exception e) {
            logger.error("SetLocationCommandHandler - failed to set location for email: {}, error: {}", dto.getEmail(), e.getMessage(), e);
            return Either.left("Failed to set location: " + e.getMessage());
        }
    }
}
