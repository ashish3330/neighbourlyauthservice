package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;
import com.neighbourly.userservice.dto.LoginResponseDTO;

public class GoogleSsoLoginCommand extends Command<LoginResponseDTO> {
    private final String googleIdToken;

    public GoogleSsoLoginCommand(String googleIdToken) {
        this.googleIdToken = googleIdToken;
    }

    public String getGoogleIdToken() {
        return googleIdToken;
    }
}