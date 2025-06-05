package com.neighbourly.userservice.controller;

import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.dto.*;
import com.neighbourly.userservice.service.UserService;
import com.neighbourly.userservice.util.ControllerUtil;
import com.neighbourly.userservice.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

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
        log.info("Received OTP request for identifier: {}", dto.getIdentifier());
        Either<String, String> result = userService.requestOtp(dto);
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        log.info("Registering user with identifier: {}", dto.getEmail());
        Either<String, UserDTO> result = userService.register(dto);
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordRequestDTO dto) {
        log.info("Setting password for identifier: {}", dto.getEmail());
        Either<String, UserDTO> result = userService.setPassword(dto);
        return controllerUtil.toResponseEntity(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        log.info("Login attempt for identifier: {}", dto.getIdentifier());
        Either<String, LoginResponseDTO> result = userService.login(dto);
        if (result.isLeft()) {
            log.warn("Login failed for identifier: {}", dto.getIdentifier());
            return controllerUtil.toResponseEntity(result);
        }
        LoginResponseDTO responseDTO = result.getRight();
        ResponseCookie accessCookie = cookieUtil.createAccessCookie(responseDTO.getAccessToken());
        ResponseCookie refreshCookie = cookieUtil.createRefreshCookie(responseDTO.getRefreshToken());
        log.info("Login successful for identifier: {}", dto.getIdentifier());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/google-sso")
    public ResponseEntity<?> googleSso(@RequestBody GoogleSsoLoginRequestDTO dto) {
        log.info("Google SSO login attempt");
        Either<String, LoginResponseDTO> result = userService.googleSsoLogin(dto.getGoogleIdToken());
        if (result.isLeft()) {
            log.warn("Google SSO login failed");
            return controllerUtil.toResponseEntity(result);
        }
        LoginResponseDTO responseDTO = result.getRight();
        ResponseCookie accessCookie = cookieUtil.createAccessCookie(responseDTO.getAccessToken());
        ResponseCookie refreshCookie = cookieUtil.createRefreshCookie(responseDTO.getRefreshToken());
        log.info("Google SSO login successful");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("refreshToken") String refreshToken) {
        log.info("Refreshing token");
        Either<String, LoginResponseDTO> result = userService.refreshToken(refreshToken);
        if (result.isLeft()) {
            log.warn("Token refresh failed");
            return controllerUtil.toResponseEntity(result);
        }
        LoginResponseDTO responseDTO = result.getRight();
        ResponseCookie accessCookie = cookieUtil.createAccessCookie(responseDTO.getAccessToken());
        log.info("Token refreshed successfully");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logging out user");
        ResponseCookie accessCookie = cookieUtil.clearAccessCookie();
        ResponseCookie refreshCookie = cookieUtil.clearRefreshCookie();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(null);
    }
}
