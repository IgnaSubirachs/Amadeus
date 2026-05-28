package com.amadeus.web.dto;

public record RecordProgressRequest(
        String userId,
        String lessonId,
        Integer score,
        Integer currentExerciseIndex,
        Boolean isCompleted) {
}
