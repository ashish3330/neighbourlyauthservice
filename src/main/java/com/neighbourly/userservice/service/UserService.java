package com.neighbourly.userservice.service;

import com.neighbourly.commonservice.dispatcher.Dispatcher;
import com.neighbourly.commonservice.errorhandling.Either;
import com.neighbourly.userservice.command.ChangePasswordCommand;
import com.neighbourly.userservice.command.GoogleSsoLoginCommand;
import com.neighbourly.userservice.command.LoginUserCommand;
import com.neighbourly.userservice.command.RegisterUserCommand;
import com.neighbourly.userservice.dto.*;
import com.neighbourly.commonservice.service.GenericService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements GenericService<UserDTO, Long, UserDTO> {

    private final Dispatcher syncDispatcher;

    public UserService(Dispatcher syncDispatcher) {
        this.syncDispatcher = syncDispatcher;
    }

    public Either<String, UserDTO> register(RegisterRequestDTO registerRequestDTO) {
        return syncDispatcher.dispatch(new RegisterUserCommand(registerRequestDTO));
    }

    public Either<String, LoginResponseDTO> login(LoginRequestDTO loginRequestDTO) {
        return syncDispatcher.dispatch(new LoginUserCommand(loginRequestDTO));
    }

    public Either<String, Void> changePassword(Long userId, ChangePasswordRequestDTO changePasswordRequestDTO) {
        return syncDispatcher.dispatch(new ChangePasswordCommand(userId, changePasswordRequestDTO));
    }

    public Either<String, LoginResponseDTO> googleSsoLogin(String googleIdToken) {
        return syncDispatcher.dispatch(new GoogleSsoLoginCommand(googleIdToken));
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
        // Implement if needed
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, List<UserDTO>> getAll() {
        // Implement if needed
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, UserDTO> update(Long id, UserDTO dto) {
        // Implement if needed
        return Either.left("Not implemented");
    }

    @Override
    public Either<String, Void> delete(Long id) {
        // Implement if needed
        return Either.left("Not implemented");
    }
}