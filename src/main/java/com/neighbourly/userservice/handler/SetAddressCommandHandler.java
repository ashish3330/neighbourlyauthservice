package com.neighbourly.userservice.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neighbourly.commonservice.dispatcher.CommandHandler;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.SetAddressCommand;
import com.neighbourly.userservice.dto.SetAddressRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.entity.Address;
import com.neighbourly.userservice.entity.User;
import com.neighbourly.userservice.repository.AddressRepository;
import com.neighbourly.userservice.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.net.URL;


@Component
public class SetAddressCommandHandler implements CommandHandler<SetAddressCommand, UserDTO> {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public SetAddressCommandHandler(UserRepository userRepository, AddressRepository addressRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Either<String, UserDTO> handle(SetAddressCommand command) {
        try {
            SetAddressRequestDTO dto = command.getSetAddressRequestDTO();

            User user = userRepository.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Address address = new Address();
            address.setStreet(dto.getStreet());
            address.setCity(dto.getCity());
            address.setState(dto.getState());
            address.setCountry(dto.getCountry());
            address.setPostalCode(dto.getPostalCode());

            // Forward geocode
            double[] latLon = geocode(address);
            user.setLatitude(latLon[0]);
            user.setLongitude(latLon[1]);
            user.setAddress(address);
            address.setUser(user);

            addressRepository.save(address);
            User savedUser = userRepository.save(user);
            return Either.right(modelMapper.map(savedUser, UserDTO.class));
        } catch (Exception e) {
            return Either.left("Failed to set address: " + e.getMessage());
        }
    }

    private double[] geocode(Address address) throws IOException {
        String q = URLEncoder.encode(String.join(" ", Arrays.asList(
                address.getStreet(), address.getCity(), address.getState(), address.getCountry()
        )), StandardCharsets.UTF_8);

        String url = "https://nominatim.openstreetmap.org/search?q=" + q + "&format=json&limit=1";

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "NeighbourlyApp");
        conn.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arr = mapper.readTree(response);
            if (!arr.isArray() || arr.size() == 0) throw new RuntimeException("Address not found");
            JsonNode first = arr.get(0);
            return new double[]{
                    Double.parseDouble(first.get("lat").asText()),
                    Double.parseDouble(first.get("lon").asText())
            };
        }
    }
}
