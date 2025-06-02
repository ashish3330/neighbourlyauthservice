package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.ChangePasswordRequestDTO;
import lombok.Getter;

@Getter
public class ChangePasswordCommand extends Command<Void> {
    private final Long userId;
    private final ChangePasswordRequestDTO changePasswordRequestDTO;

    public ChangePasswordCommand(Long userId, ChangePasswordRequestDTO changePasswordRequestDTO) {
        this.userId = userId;
        this.changePasswordRequestDTO = changePasswordRequestDTO;
    }

}