package com.neighbourly.userservice.controller;

import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.dto.SetAddressRequestDTO;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import com.neighbourly.userservice.service.UserLocationService;
import com.neighbourly.userservice.util.ControllerUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/location")
public class UserLocationController {

    private final UserLocationService userLocationService;
    private final ControllerUtil controllerUtil;

    public UserLocationController(UserLocationService userLocationService, ControllerUtil controllerUtil) {
        this.userLocationService = userLocationService;
        this.controllerUtil = controllerUtil;
    }

    @PostMapping("/set-location")
    public ResponseEntity<?> setLocation(@RequestBody SetLocationRequestDTO dto) {
        Either<String, UserDTO> result = userLocationService.setLocation(dto);
        return controllerUtil.toResponseEntity(result);

    }

    @PostMapping("/set-address")
    public ResponseEntity<?> setAddress(@RequestBody SetAddressRequestDTO dto) {
        Either<String, UserDTO> result = userLocationService.setAddress(dto);
        return controllerUtil.toResponseEntity(result);

    }
}