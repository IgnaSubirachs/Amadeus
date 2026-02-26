package com.amadeus.web.dto;

public record ExerciseDTO(
        String id,
        String lessonId,
        String type,
        String difficulty,
        Integer orderNumber,
        String questionData,
        String correctAnswer,
        Integer maxPoints) {
}
