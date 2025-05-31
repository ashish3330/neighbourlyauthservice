package com.neighbourly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequestDTO {
    private String email;
    private String phoneNumber;
    private String name;
    private String password;
    private String otp;

}