package com.amadeus.web.mapper;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.ExerciseType;
import com.amadeus.web.dto.CreateExerciseRequest;
import com.amadeus.web.dto.ExerciseDTO;

import java.util.Locale;

public class ExerciseMapper {

    public static Exercise toEntity(CreateExerciseRequest request) {
        if (request == null)
            return null;

        Exercise exercise = new Exercise();
        exercise.setLessonId(request.lessonId());
        exercise.setType(ExerciseType.valueOf(request.type().trim().toUpperCase(Locale.ROOT)));
        exercise.setDifficulty(request.difficulty());
        exercise.setOrderNumber(request.orderNumber());
        exercise.setQuestionData(request.questionData());
        exercise.setCorrectAnswer(request.correctAnswer());
        exercise.setMaxPoints(request.maxPoints() != null ? request.maxPoints() : 10);

        return exercise;
    }

    public static ExerciseDTO toDTO(Exercise exercise) {
        if (exercise == null)
            return null;

        return new ExerciseDTO(
                exercise.getId(),
                exercise.getLessonId(),
                exercise.getType().name(),
                exercise.getDifficulty(),
                exercise.getOrderNumber(),
                exercise.getQuestionData(),
                exercise.getCorrectAnswer(),
                exercise.getMaxPoints());
    }
}