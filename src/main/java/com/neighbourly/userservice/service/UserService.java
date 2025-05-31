package com.neighbourly.userservice.service;

import com.neighbourly.commonservice.dispatcher.Dispatcher;
import com.neighbourly.commonservice.dispatcher.SyncDispatcher;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.*;
import com.neighbourly.userservice.dto.*;
import com.neighbourly.commonservice.service.GenericService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements GenericService<UserDTO, Long, UserDTO> {

    private final SyncDispatcher syncDispatcher;

    public UserService(@Qualifier("userServiceSyncDispatcher")SyncDispatcher syncDispatcher) {
        this.syncDispatcher = syncDispatcher;
    }

    public Either<String, String> requestOtp(RequestOtpDTO requestOtpDTO) {
        return syncDispatcher.dispatch(new RequestOtpCommand(requestOtpDTO));
    }

    public Either<String, UserDTO> register(RegisterRequestDTO registerRequestDTO) {
        return syncDispatcher.dispatch(new RegisterUserCommand(registerRequestDTO));
    }

    public Either<String, UserDTO> setPassword(SetPasswordRequestDTO setPasswordRequestDTO) {
        return syncDispatcher.dispatch(new SetPasswordCommand(setPasswordRequestDTO));
    }

    public Either<String, LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        return syncDispatcher.dispatch(new LoginUserCommand(loginRequestDTO));
    }

    public Either<String, LoginResponseDTO> refreshToken(String refreshToken) {
        return syncDispatcher.dispatch(new RefreshTokenCommand(refreshToken));
    }


    public Either<String, LoginResponseDTO> googleSsoLogin(String googleIdToken) {
        return syncDispatcher.dispatch(new GoogleSsoLoginCommand(googleIdToken));
    }

    public Either<String, UserDTO> setLocation(SetLocationRequestDTO setLocationRequestDTO) {
        return syncDispatcher.dispatch(new SetLocationCommand(setLocationRequestDTO));
    }

    public Either<String, UserDTO> setAddress(SetAddressRequestDTO setAddressRequestDTO) {
        return syncDispatcher.dispatch(new SetAddressCommand(setAddressRequestDTO));
    }

    public Either<String, Void> changePassword(Long userId, ChangePasswordRequestDTO changePasswordRequestDTO) {
        return syncDispatcher.dispatch(new ChangePasswordCommand(userId, changePasswordRequestDTO));
    }

    @Override
    public Either<String, UserDTO> create(UserDTO dto) {
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setName(dto.getName());
        registerRequestDTO.setEmail(dto.getEmail());
        registerRequestDTO.setPhoneNumber(dto.getPhoneNumber());
        // Password handling omitted for GenericService compatibility
        return register(registerRequestDTO);
    }

    @Override
    public Either<String, Optional<UserDTO>> getById(Long id) {
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, List<UserDTO>> getAll() {
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, UserDTO> update(Long id, UserDTO dto) {
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, Void> delete(Long id) {
        return Either.left("Not implemented");
    }
}