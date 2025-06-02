package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.SetPasswordRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import lombok.Getter;

@Getter
public class SetPasswordCommand extends Command<UserDTO> {
    private final SetPasswordRequestDTO setPasswordRequestDTO;

    public SetPasswordCommand(SetPasswordRequestDTO setPasswordRequestDTO) {
        this.setPasswordRequestDTO = setPasswordRequestDTO;
    }

}