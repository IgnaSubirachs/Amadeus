package com.amadeus.web.dto;

public record AuthRequest(
        String email,
        String password) {
}
