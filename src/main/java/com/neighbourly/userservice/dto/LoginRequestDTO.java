package com.neighbourly.userservice.dto;

public class LoginRequestDTO {
    private String identifier; // Email or phone number
    private String password;
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}