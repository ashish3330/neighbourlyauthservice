package com.neighbourly.userservice.controller;

import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.*;
import com.neighbourly.userservice.dto.*;
import com.neighbourly.userservice.handler.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RequestOtpCommandHandler requestOtpHandler;
    private final RegisterUserCommandHandler registerHandler;
    private final SetPasswordCommandHandler setPasswordHandler;
    private final LoginUserCommandHandler loginHandler;
    private final GoogleSsoLoginCommandHandler googleSsoHandler;

    public AuthController(RequestOtpCommandHandler requestOtpHandler,
                          RegisterUserCommandHandler registerHandler,
                          SetPasswordCommandHandler setPasswordHandler,
                          LoginUserCommandHandler loginHandler,
                          GoogleSsoLoginCommandHandler googleSsoHandler) {
        this.requestOtpHandler = requestOtpHandler;
        this.registerHandler = registerHandler;
        this.setPasswordHandler = setPasswordHandler;
        this.loginHandler = loginHandler;
        this.googleSsoHandler = googleSsoHandler;
    }

    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOtp(@RequestBody RequestOtpDTO dto) {
        Either<String, String> result = requestOtpHandler.handle(new RequestOtpCommand(dto));
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        Either<String, UserDTO> result = registerHandler.handle(new RegisterUserCommand(dto));
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordRequestDTO dto) {
        Either<String, UserDTO> result = setPasswordHandler.handle(new SetPasswordCommand(dto));
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        Either<String, LoginResponseDTO> result = loginHandler.handle(new LoginUserCommand(dto));
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }

    @PostMapping("/google-sso")
    public ResponseEntity<?> googleSso(@RequestBody GoogleSsoLoginRequestDTO dto) {
        Either<String, LoginResponseDTO> result = googleSsoHandler.handle(new GoogleSsoLoginCommand(dto.getGoogleIdToken()));
        return result.isRight()
                ? ResponseEntity.ok(result.getRight())
                : ResponseEntity.badRequest().body(result.getLeft());
    }
}

