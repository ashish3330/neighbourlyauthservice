package com.neighbourly.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SetAddressRequestDTO {
    private String email;
    private String street;
    private String city;
    private String state;
    private String country;
    private String postalCode;

}
