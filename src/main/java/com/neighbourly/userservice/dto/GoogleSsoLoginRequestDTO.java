package com.neighbourly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoogleSsoLoginRequestDTO {
    private String googleIdToken;

}