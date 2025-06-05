package com.neighbourly.userservice.contants;

public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    SERVICE_PROVIDER("ROLE_SERVICE_PROVIDER"),
    SUPPORT("ROLE_SUPPORT");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
