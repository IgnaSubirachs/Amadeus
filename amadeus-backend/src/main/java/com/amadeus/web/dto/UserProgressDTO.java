package com.amadeus.web.dto;

import java.time.LocalDateTime;

public record UserProgressDTO(
        String id,
        String userId,
        String lessonId,
        Integer score,
        Integer currentExerciseIndex,
        Boolean isCompleted,
        LocalDateTime completedAt) {
}
