package com.amadeus.web.dto;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.Lesson;
import com.amadeus.domain.model.UserProgress;

import java.util.List;

public record LessonSessionDTO(
        String lessonId,
        String title,
        String description,
        String topic,
        Integer estimatedMinutes,
        Integer currentExerciseIndex,
        Integer totalScore,
        Boolean completed,
        List<ExerciseViewDTO> exercises) {

    public static LessonSessionDTO from(Lesson lesson, List<Exercise> exercises, UserProgress progress) {
        return new LessonSessionDTO(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getMusicTheoryTopic(),
                lesson.getEstimatedMinutes(),
                progress.getCurrentExerciseIndex(),
                progress.getTotalScore(),
                progress.getIsCompleted(),
                exercises.stream().map(ExerciseViewDTO::from).toList());
    }
}