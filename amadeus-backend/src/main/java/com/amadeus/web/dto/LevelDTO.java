package com.amadeus.web.dto;

public record LevelDTO(
        String id,
        String name,
        String description,
        Integer orderNumber,
        String difficulty,
        String imageUrl) {
}
