package com.neighbourly.userservice.dto;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken; // Added
    private UserDTO user;
    private Cookie accessCookie;
    private Cookie refreshCookie;
    private String error;




    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Cookie getAccessCookie() {
        return accessCookie;
    }

    public void setAccessCookie(Cookie accessCookie) {
        this.accessCookie = accessCookie;
    }

    public Cookie getRefreshCookie() {
        return refreshCookie;
    }

    public void setRefreshCookie(Cookie refreshCookie) {
        this.refreshCookie = refreshCookie;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}