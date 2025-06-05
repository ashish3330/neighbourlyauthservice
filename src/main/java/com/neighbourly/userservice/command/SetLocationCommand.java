package com.neighbourly.userservice.command;


import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.SetLocationRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;
import lombok.Getter;

@Getter
public class SetLocationCommand extends Command<UserDTO> {
    private final SetLocationRequestDTO setLocationRequestDTO;

    public SetLocationCommand(SetLocationRequestDTO setLocationRequestDTO) {
        this.setLocationRequestDTO = setLocationRequestDTO;
    }

}