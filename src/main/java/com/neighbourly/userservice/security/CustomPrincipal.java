package com.neighbourly.userservice.security;

import java.util.List;

public record CustomPrincipal(Long userId, String username, List<String> roles) {
}