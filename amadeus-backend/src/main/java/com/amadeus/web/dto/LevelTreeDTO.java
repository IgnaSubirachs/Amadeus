package com.amadeus.web.dto;

import java.util.List;

public record LevelTreeDTO(
        String id,
        Integer orderNumber,
        String name,
        String description,
        String difficulty,
        List<LessonTreeDTO> lessons) {
}