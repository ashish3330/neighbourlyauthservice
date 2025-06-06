package com.neighbourly.userservice.handler;

import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SetAddressCommand;
import com.neighbourly.userservice.dto.SetAddressRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.Address;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.AddressRepository;
import com.neighbourly.userservice.repository.UserRepository;
import com.neighbourly.userservice.service.GeocodeService;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SetAddressCommandHandler implements CommandHandler<SetAddressCommand, UserDTO> {

    private static final Logger logger = LoggerFactory.getLogger(SetAddressCommandHandler.class);

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;
    private final GeocodeService geocodeService;
    private static final GeometryFactory geometryFactory = new GeometryFactory();


    public SetAddressCommandHandler(UserRepository userRepository, AddressRepository addressRepository,
                                    ModelMapper modelMapper, GeocodeService geocodeService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
        this.geocodeService = geocodeService;
    }

    @Override
    public Either<String, UserDTO> handle(SetAddressCommand command) {
        try {
            SetAddressRequestDTO dto = command.getSetAddressRequestDTO();
            logger.info("SetAddressCommandHandler - setting address for email: {}", dto.getEmail());

            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("SetAddressCommandHandler - user not found for email: {}", dto.getEmail());
                        return new IllegalArgumentException("User not found");
                    });

            Address address = new Address();
            address.setStreet(dto.getStreet());
            address.setCity(dto.getCity());
            address.setState(dto.getState());
            address.setCountry(dto.getCountry());
            address.setPostalCode(dto.getPostalCode());

            double[] latLon = geocodeService.geocode(address);
            Point point = geometryFactory.createPoint(new Coordinate(latLon[1], latLon[0]));

            user.setLocation(point);
            user.setAddress(address);
            address.setUser(user);

            addressRepository.save(address);
            User savedUser = userRepository.save(user);

            logger.info("SetAddressCommandHandler - address set successfully for email: {}", dto.getEmail());
            return Either.right(modelMapper.map(savedUser, UserDTO.class));
        } catch (Exception e) {
            logger.error("SetAddressCommandHandler - failed to set address: {}", e.getMessage(), e);
            return Either.left("Failed to set address: " + e.getMessage());
        }
    }
}
