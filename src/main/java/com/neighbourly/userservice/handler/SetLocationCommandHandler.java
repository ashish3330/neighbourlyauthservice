package com.neighbourly.userservice.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SetLocationCommand;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.Address;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.AddressRepository;
import com.neighbourly.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import java.net.URL;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;

@Component
public class SetLocationCommandHandler implements CommandHandler<SetLocationCommand, UserDTO> {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public SetLocationCommandHandler(UserRepository userRepository, AddressRepository addressRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Either<String, UserDTO> handle(SetLocationCommand command) {
        try {
            SetLocationRequestDTO dto = command.getSetLocationRequestDTO();

            if (dto.getLatitude() == null || dto.getLongitude() == null)
                return Either.left("Latitude and longitude must not be null");

            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Address address = reverseGeocode(dto.getLatitude(), dto.getLongitude());

            user.setLatitude(dto.getLatitude());
            user.setLongitude(dto.getLongitude());
            user.setAddress(address);
            address.setUser(user);

            addressRepository.save(address);
            User savedUser = userRepository.save(user);
            return Either.right(modelMapper.map(savedUser, UserDTO.class));
        } catch (Exception e) {
            return Either.left("Failed to set location: " + e.getMessage());
        }
    }

    private Address reverseGeocode(Double lat, Double lon) throws IOException {
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "NeighbourlyApp");
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            JsonNode addr = json.get("address");

            Address address = new Address();
            address.setStreet(addr.path("road").asText(""));
            address.setCity(addr.path("city").asText(""));
            address.setState(addr.path("state").asText(""));
            address.setCountry(addr.path("country").asText(""));
            address.setPostalCode(addr.path("postcode").asText(""));
            return address;
        }
    }
}
