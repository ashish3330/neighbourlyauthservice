package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.RegisterRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import lombok.Getter;

@Getter
public class RegisterUserCommand extends Command<UserDTO> {
    private final RegisterRequestDTO registerRequestDTO;

    public RegisterUserCommand(RegisterRequestDTO registerRequestDTO) {
        this.registerRequestDTO = registerRequestDTO;
    }

}