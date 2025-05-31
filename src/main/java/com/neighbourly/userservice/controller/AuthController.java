package com.neighbourly.userservice.controller;

import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.dto.*;
import com.neighbourly.userservice.service.UserService;
import com.neighbourly.userservice.util.ControllerUtil;
import com.neighbourly.userservice.util.CookieUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final ControllerUtil controllerUtil;
    private final CookieUtil cookieUtil;

    public AuthController(UserService userService, ControllerUtil controllerUtil, CookieUtil cookieUtil) {
        this.userService = userService;
        this.controllerUtil = controllerUtil;
        this.cookieUtil = cookieUtil;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody RequestOtpDTO dto) {
        Either<String, String> result = userService.requestOtp(dto);
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        Either<String, UserDTO> result = userService.register(dto);
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordRequestDTO dto) {
        Either<String, UserDTO> result = userService.setPassword(dto);
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        Either<String, LoginResponseDTO> result = userService.login(dto);
        if (result.isLeft()) {
            return controllerUtil.toResponseEntity(result);
        }
        LoginResponseDTO responseDTO = result.getRight();
        ResponseCookie accessCookie = cookieUtil.createAccessCookie(responseDTO.getAccessToken());
        ResponseCookie refreshCookie = cookieUtil.createRefreshCookie(responseDTO.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/google-sso")
    public ResponseEntity<?> googleSso(@RequestBody GoogleSsoLoginRequestDTO dto) {
        Either<String, LoginResponseDTO> result = userService.googleSsoLogin(dto.getGoogleIdToken());
        if (result.isLeft()) {
            return controllerUtil.toResponseEntity(result);
        }
        LoginResponseDTO responseDTO = result.getRight();
        ResponseCookie accessCookie = cookieUtil.createAccessCookie(responseDTO.getAccessToken());
        ResponseCookie refreshCookie = cookieUtil.createRefreshCookie(responseDTO.getRefreshToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String refreshToken) {
        Either<String, LoginResponseDTO> result = userService.refreshToken(refreshToken);
        if (result.isLeft()) {
            return controllerUtil.toResponseEntity(result);
        }
        LoginResponseDTO responseDTO = result.getRight();
        ResponseCookie accessCookie = cookieUtil.createAccessCookie(responseDTO.getAccessToken());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie accessCookie = cookieUtil.clearAccessCookie();
        ResponseCookie refreshCookie = cookieUtil.clearRefreshCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(null);
    }
}