package com.neighbourly.userservice.dto;

public class GoogleSsoLoginRequestDTO {
    private String googleIdToken;

    public String getGoogleIdToken() {
        return googleIdToken;
    }

    public void setGoogleIdToken(String googleIdToken) {
        this.googleIdToken = googleIdToken;
    }
}