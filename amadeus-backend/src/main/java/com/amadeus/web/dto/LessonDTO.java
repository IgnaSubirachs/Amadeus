package com.amadeus.web.dto;

public record LessonDTO(
        String id,
        String levelId,
        String title,
        String description,
        String content,
        String musicTheoryTopic,
        Integer orderNumber) {
}
