package com.amadeus.domain.service;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.ExerciseType;
import com.amadeus.domain.model.Lesson;
import com.amadeus.domain.model.UserProgress;
import com.amadeus.domain.repository.ExerciseRepository;
import com.amadeus.domain.repository.LessonRepository;
import com.amadeus.domain.repository.UserProgressRepository;
import com.amadeus.web.dto.ExerciseResultRequest;
import com.amadeus.web.dto.ProgressSummaryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LessonSessionService Tests")
class LessonSessionServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private UserProgressRepository userProgressRepository;

    private LessonSessionService lessonSessionService;

    private Lesson lesson;
    private Exercise exercise;
    private UserProgress progress;

    @BeforeEach
    void setUp() {
        lessonSessionService = new LessonSessionService(
                lessonRepository,
                exerciseRepository,
                userProgressRepository,
                new ObjectMapper());

        lesson = Lesson.builder()
                .id("lesson-1")
                .levelId("level-1")
                .orderNumber(1)
                .title("Notes")
                .musicTheoryTopic("notes")
                .build();

        exercise = Exercise.builder()
                .id("exercise-1")
                .lessonId("lesson-1")
                .type(ExerciseType.MULTIPLE_CHOICE)
                .orderNumber(1)
                .questionData("{" + "\"question\":\"Quina nota es aquesta?\",\"options\":[\"Do\",\"Re\",\"Mi\"]}")
                .correctAnswer("{" + "\"answer\":\"Do\"}")
                .maxPoints(10)
                .build();

        progress = UserProgress.builder()
                .id("progress-1")
                .userId("user-1")
                .lessonId("lesson-1")
                .isUnlocked(true)
                .currentExerciseIndex(0)
                .totalScore(0)
                .isCompleted(false)
                .build();
    }

    @Test
    @DisplayName("Should evaluate a correct answer on the server")
    void shouldEvaluateCorrectAnswerOnServer() {
        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(lesson));
        when(exerciseRepository.findByLessonIdOrderByOrderNumberAsc("lesson-1")).thenReturn(List.of(exercise));
        when(userProgressRepository.findByUserIdAndLessonId("user-1", "lesson-1")).thenReturn(Optional.of(progress));
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonRepository.findByLevelIdOrderByOrderNumberAsc("level-1")).thenReturn(List.of(lesson));

        ProgressSummaryDTO result = lessonSessionService.submitAnswer(
                "user-1",
                "lesson-1",
                new ExerciseResultRequest("exercise-1", " do "));

        assertTrue(result.answerCorrect());
        assertEquals(10, result.awardedPoints());
        assertEquals(10, result.totalScore());
        assertTrue(result.completed());
        verify(userProgressRepository).save(any(UserProgress.class));
    }

    @Test
    @DisplayName("Should reject an incorrect answer on the server")
    void shouldRejectIncorrectAnswerOnServer() {
        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(lesson));
        when(exerciseRepository.findByLessonIdOrderByOrderNumberAsc("lesson-1")).thenReturn(List.of(exercise));
        when(userProgressRepository.findByUserIdAndLessonId("user-1", "lesson-1")).thenReturn(Optional.of(progress));
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonRepository.findByLevelIdOrderByOrderNumberAsc("level-1")).thenReturn(List.of(lesson));

        ProgressSummaryDTO result = lessonSessionService.submitAnswer(
                "user-1",
                "lesson-1",
                new ExerciseResultRequest("exercise-1", "Re"));

        assertFalse(result.answerCorrect());
        assertEquals(0, result.awardedPoints());
        assertEquals(0, result.totalScore());
        assertTrue(result.completed());
    }

    @Test
    @DisplayName("Should evaluate rhythm answers as arrays")
    void shouldEvaluateRhythmAnswersAsArrays() {
        Exercise rhythmExercise = Exercise.builder()
                .id("exercise-2")
                .lessonId("lesson-1")
                .type(ExerciseType.RHYTHM_READING)
                .orderNumber(1)
                .questionData("{" + "\"question\":\"Llegeix el ritme\",\"pattern\":[\"ta\",\"ta-a\"]}")
                .correctAnswer("{" + "\"answers\":[\"ta\",\"ta-a\"]}")
                .maxPoints(15)
                .build();

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(lesson));
        when(exerciseRepository.findByLessonIdOrderByOrderNumberAsc("lesson-1")).thenReturn(List.of(rhythmExercise));
        when(userProgressRepository.findByUserIdAndLessonId("user-1", "lesson-1")).thenReturn(Optional.of(progress));
        when(userProgressRepository.save(any(UserProgress.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonRepository.findByLevelIdOrderByOrderNumberAsc("level-1")).thenReturn(List.of(lesson));

        ProgressSummaryDTO result = lessonSessionService.submitAnswer(
                "user-1",
                "lesson-1",
                new ExerciseResultRequest("exercise-2", "[\"ta-a\", \"ta\"]"));

        assertTrue(result.answerCorrect());
        assertEquals(15, result.awardedPoints());
    }

    @Test
    @DisplayName("Should fail when exercise schema is invalid")
    void shouldFailWhenExerciseSchemaIsInvalid() {
        Exercise invalidExercise = Exercise.builder()
                .id("exercise-invalid")
                .lessonId("lesson-1")
                .type(ExerciseType.MULTIPLE_CHOICE)
                .orderNumber(1)
                .questionData("{" + "\"question\":\"Tria\"}")
                .correctAnswer("{" + "\"answer\":\"Do\"}")
                .build();

        when(lessonRepository.findById("lesson-1")).thenReturn(Optional.of(lesson));
        when(exerciseRepository.findByLessonIdOrderByOrderNumberAsc("lesson-1")).thenReturn(List.of(invalidExercise));
        when(userProgressRepository.findByUserIdAndLessonId("user-1", "lesson-1")).thenReturn(Optional.of(progress));

        assertThrows(IllegalArgumentException.class, () -> lessonSessionService.submitAnswer(
                "user-1",
                "lesson-1",
                new ExerciseResultRequest("exercise-invalid", "Do")));
    }
}