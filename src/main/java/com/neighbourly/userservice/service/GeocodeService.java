package com.neighbourly.userservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neighbourly.userservice.entity.Address;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class GeocodeService {

    private static final Logger logger = LoggerFactory.getLogger(GeocodeService.class);
    private static final String USER_AGENT = "NeighbourlyApp";

    public double[] geocode(Address address) throws IOException {
        // Primary query with full address including postal code
        String query = URLEncoder.encode(String.join(" ", Arrays.asList(
                address.getStreet(), address.getCity(), address.getState(), address.getCountry(), address.getPostalCode()
        )), StandardCharsets.UTF_8);

        String urlStr = "https://nominatim.openstreetmap.org/search?q=" + query + "&format=json&limit=1";

        logger.debug("Geocoding address with URL: {}", urlStr);

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            logger.debug("Raw API response geocode: {}", response);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arr = mapper.readTree(response);

            // Fallback query if primary fails
            if (!arr.isArray() || arr.isEmpty()) {
                logger.warn("Geocoding failed: no results found for address: {}. Trying fallback.", address);
                String fallbackQuery = URLEncoder.encode(String.join(" ", Arrays.asList(
                        address.getCity(), address.getState(), address.getCountry(), address.getPostalCode()
                )), StandardCharsets.UTF_8);
                urlStr = "https://nominatim.openstreetmap.org/search?q=" + fallbackQuery + "&format=json&limit=1";
                conn = (HttpURLConnection) new URL(urlStr).openConnection();
                conn.setRequestProperty("User-Agent", USER_AGENT);
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                try (BufferedReader fallbackReader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    response = fallbackReader.lines().collect(Collectors.joining());
                    logger.debug("Fallback API response: {}", response);
                    arr = mapper.readTree(response);
                    if (!arr.isArray() || arr.isEmpty()) {
                        logger.error("Geocoding failed: no results found for fallback query: {}", fallbackQuery);
                        throw new RuntimeException("Address not found even with fallback query");
                    }
                }
            }

            JsonNode first = arr.get(0);
            double lat = Double.parseDouble(first.get("lat").asText());
            double lon = Double.parseDouble(first.get("lon").asText());

            logger.debug("Geocoding successful: lat={}, lon={}", lat, lon);

            return new double[]{lat, lon};
        } catch (IOException e) {
            logger.error("Geocoding failed for address: {}. Error: {}", address, e.getMessage());
            throw e;
        }
    }

    public Address reverseGeocode(Double lat, Double lon) throws IOException {
        String urlStr = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" + lat + "&lon=" + lon;

        logger.debug("Reverse geocoding with URL: {}", urlStr);

        HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
        conn.setRequestProperty("User-Agent", USER_AGENT);
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String response = reader.lines().collect(Collectors.joining());
            logger.debug("Raw API response reverse geocode: {}", response);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            JsonNode addr = json.get("address");

            if (addr == null || addr.isEmpty()) {
                logger.warn("Reverse geocoding failed: no address found for lat={}, lon={}", lat, lon);
                throw new RuntimeException("Address not found for provided coordinates");
            }

            Address address = new Address();
            address.setStreet(addr.path("road").asText(""));
            address.setCity(addr.path("city").asText(""));
            address.setState(addr.path("state").asText(""));
            address.setCountry(addr.path("country").asText(""));
            address.setPostalCode(addr.path("postcode").asText(""));

            logger.debug("Reverse geocoding successful: {}", address);

            return address;
        } catch (IOException e) {
            logger.error("Reverse geocoding failed for lat={}, lon={}. Error: {}", lat, lon, e.getMessage());
            throw e;
        }
    }
}