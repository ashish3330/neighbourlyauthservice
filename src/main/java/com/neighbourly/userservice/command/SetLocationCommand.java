package com.neighbourly.userservice.command;


import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;

public class SetLocationCommand extends Command<UserDTO> {
    private final SetLocationRequestDTO setLocationRequestDTO;

    public SetLocationCommand(SetLocationRequestDTO setLocationRequestDTO) {
        this.setLocationRequestDTO = setLocationRequestDTO;
    }

    public SetLocationRequestDTO getSetLocationRequestDTO() {
        return setLocationRequestDTO;
    }
}