package com.amadeus.web.dto;

public record CreateLessonRequest(
        String levelId,
        String title,
        String description,
        String content,
        String musicTheoryTopic,
        Integer orderNumber) {
}
