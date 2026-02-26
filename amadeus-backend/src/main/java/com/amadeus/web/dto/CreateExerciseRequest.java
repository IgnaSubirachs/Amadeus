package com.amadeus.web.dto;

public record CreateExerciseRequest(
        String lessonId,
        String type,
        String difficulty,
        Integer orderNumber,
        String questionData,
        String correctAnswer,
        Integer maxPoints) {
}
