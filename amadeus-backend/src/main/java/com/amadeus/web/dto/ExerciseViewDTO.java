package com.amadeus.web.dto;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.ExerciseType;

public record ExerciseViewDTO(
        String id,
        ExerciseType type,
        String difficulty,
        Integer orderNumber,
        String questionData,
        Integer maxPoints) {

    public static ExerciseViewDTO from(Exercise exercise) {
        return new ExerciseViewDTO(
                exercise.getId(),
                exercise.getType(),
                exercise.getDifficulty(),
                exercise.getOrderNumber(),
                exercise.getQuestionData(),
                exercise.getMaxPoints());
    }
}