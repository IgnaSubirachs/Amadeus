package com.amadeus.web.dto;

public record AuthResponse(
        String token,
        UserDTO user) {
}
