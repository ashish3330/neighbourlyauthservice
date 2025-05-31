package com.neighbourly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SetPasswordRequestDTO {
    private String email;
    private String password;

}