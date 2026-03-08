package com.amadeus.web.dto;

public record LessonTreeDTO(
        String id,
        Integer orderNumber,
        String title,
        String description,
        String topic,
        Integer estimatedMinutes,
        long exerciseCount) {
}