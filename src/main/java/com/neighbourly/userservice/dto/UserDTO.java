package com.neighbourly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;

}