package com.amadeus.domain.service;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public Exercise createExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Transactional(readOnly = true)
    public List<Exercise> getExercisesByLesson(String lessonId) {
        return exerciseRepository.findByLessonIdOrderByOrderNumberAsc(lessonId);
    }

    @Transactional(readOnly = true)
    public Exercise getExerciseById(String id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejercicio no encontrado: " + id));
    }

    public Exercise updateExercise(String id, Exercise exerciseDetails) {
        Exercise exercise = getExerciseById(id);

        exercise.setType(exerciseDetails.getType());
        exercise.setDifficulty(exerciseDetails.getDifficulty());
        exercise.setOrderNumber(exerciseDetails.getOrderNumber());
        exercise.setQuestionData(exerciseDetails.getQuestionData());
        exercise.setCorrectAnswer(exerciseDetails.getCorrectAnswer());
        exercise.setMaxPoints(exerciseDetails.getMaxPoints());

        return exerciseRepository.save(exercise);
    }

    public void deleteExercise(String id) {
        Exercise exercise = getExerciseById(id);
        exerciseRepository.delete(exercise);
    }
}
