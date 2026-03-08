package com.amadeus.web.dto;

public record CreateLevelRequest(
        String name,
        String description,
        Integer orderNumber,
        String imageUrl) {
}
