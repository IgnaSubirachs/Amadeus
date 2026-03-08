package com.amadeus.web.dto;

import com.amadeus.domain.model.UserProgress;

import java.time.LocalDateTime;

public record ProgressSummaryDTO(
        String lessonId,
        Boolean unlocked,
        Boolean completed,
        Integer currentExerciseIndex,
        Integer totalExercises,
        Integer totalScore,
        Boolean answerCorrect,
        Integer awardedPoints,
        LocalDateTime completedAt) {

    public static ProgressSummaryDTO from(UserProgress progress, long totalExercises) {
        return new ProgressSummaryDTO(
                progress.getLessonId(),
                progress.getIsUnlocked(),
                progress.getIsCompleted(),
                progress.getCurrentExerciseIndex(),
                Math.toIntExact(totalExercises),
                progress.getTotalScore(),
                null,
                0,
                progress.getCompletedAt());
    }

    public static ProgressSummaryDTO from(UserProgress progress, long totalExercises, boolean answerCorrect, int awardedPoints) {
        return new ProgressSummaryDTO(
                progress.getLessonId(),
                progress.getIsUnlocked(),
                progress.getIsCompleted(),
                progress.getCurrentExerciseIndex(),
                Math.toIntExact(totalExercises),
                progress.getTotalScore(),
                answerCorrect,
                awardedPoints,
                progress.getCompletedAt());
    }
}