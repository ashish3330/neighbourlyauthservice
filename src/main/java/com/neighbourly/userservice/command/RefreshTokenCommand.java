package com.neighbourly.userservice.command;

import com.neighbourly.commonservice.dispatcher.Command;

public class RefreshTokenCommand extends Command<String> {
    private final String token;

    public RefreshTokenCommand(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}