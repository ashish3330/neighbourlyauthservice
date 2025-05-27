package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.SetAddressRequestDTO;
import com.neighbourly.userservice.dto.UserDTO;

public class SetAddressCommand extends Command<UserDTO> {
    private final SetAddressRequestDTO setAddressRequestDTO;

    public SetAddressCommand(SetAddressRequestDTO dto) {
        this.setAddressRequestDTO = dto;
    }

    public SetAddressRequestDTO getSetAddressRequestDTO() {
        return setAddressRequestDTO;
    }
}
