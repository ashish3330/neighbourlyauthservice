package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.LoginRequestDTO;
import com.neighbourly.userservice.dto.LoginResponseDTO;

public class LoginUserCommand extends Command<LoginResponseDTO> {
    private final LoginRequestDTO loginRequestDTO;

    public LoginUserCommand(LoginRequestDTO loginRequestDTO) {
        this.loginRequestDTO = loginRequestDTO;
    }

    public LoginRequestDTO getLoginRequestDTO() {
        return loginRequestDTO;
    }
}