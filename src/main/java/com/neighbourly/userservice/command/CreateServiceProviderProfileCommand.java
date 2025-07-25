package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.CreateServiceProviderProfileRequestDTO;
import com.neighbourly.userservice.dto.ServiceProviderProfileDTO;
import lombok.Getter;

@Getter
public class CreateServiceProviderProfileCommand extends Command<ServiceProviderProfileDTO> {
    private final CreateServiceProviderProfileRequestDTO requestDTO;

    public CreateServiceProviderProfileCommand(CreateServiceProviderProfileRequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

}