package com.neighbourly.userservice.dto;

import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
}