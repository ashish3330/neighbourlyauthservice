package com.neighbourly.userservice.dto;

public class RequestOtpDTO {
    private String identifier; // Email or phone number

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}