package com.neighbourly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SetLocationRequestDTO {
    private String email;
    private Double latitude;
    private Double longitude;

}