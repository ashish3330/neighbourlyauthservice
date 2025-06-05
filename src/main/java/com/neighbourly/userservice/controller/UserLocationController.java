package com.neighbourly.userservice.controller;

import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.dto.SetAddressRequestDTO;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.service.UserLocationService;
import com.neighbourly.userservice.util.ControllerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/location")
public class UserLocationController {

    private static final Logger log = LoggerFactory.getLogger(UserLocationController.class);

    private final UserLocationService userLocationService;
    private final ControllerUtil controllerUtil;

    public UserLocationController(UserLocationService userLocationService, ControllerUtil controllerUtil) {
        this.userLocationService = userLocationService;
        this.controllerUtil = controllerUtil;
    }

    @PostMapping("/set-location")
    public ResponseEntity<?> setLocation(@RequestBody SetLocationRequestDTO dto) {
        log.info("Setting location for userId: {}", dto.getEmail());
        Either<String, UserDTO> result = userLocationService.setLocation(dto);
        if (result.isLeft()) {
            log.warn("Failed to set location for userId: {}. Reason: {}", dto.getEmail(), result.getLeft());
        } else {
            log.info("Successfully set location for userId: {}", dto.getEmail());
        }
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/set-address")
    public ResponseEntity<?> setAddress(@RequestBody SetAddressRequestDTO dto) {
        log.info("Setting address for userId: {}", dto.getEmail());
        Either<String, UserDTO> result = userLocationService.setAddress(dto);
        if (result.isLeft()) {
            log.warn("Failed to set address for userId: {}. Reason: {}", dto.getEmail(), result.getLeft());
        } else {
            log.info("Successfully set address for userId: {}", dto.getEmail());
        }
        return controllerUtil.toResponseEntity(result);
    }
}
