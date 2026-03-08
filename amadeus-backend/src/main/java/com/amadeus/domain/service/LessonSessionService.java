package com.amadeus.domain.service;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.ExerciseType;
import com.amadeus.domain.model.Lesson;
import com.amadeus.domain.model.UserProgress;
import com.amadeus.domain.repository.ExerciseRepository;
import com.amadeus.domain.repository.LessonRepository;
import com.amadeus.domain.repository.UserProgressRepository;
import com.amadeus.web.dto.ExerciseResultRequest;
import com.amadeus.web.dto.LessonSessionDTO;
import com.amadeus.web.dto.ProgressSummaryDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonSessionService {

    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserProgressRepository userProgressRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public LessonSessionDTO startLesson(String userId, String lessonId) {
        Lesson lesson = findLesson(lessonId);
        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderByOrderNumberAsc(lessonId);
        exercises.forEach(this::validateExerciseSchema);

        UserProgress progress = userProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> createInitialProgress(userId, lesson));

        if (!progress.getIsUnlocked()) {
            throw new IllegalArgumentException("Lesson is locked for user: " + userId);
        }

        return LessonSessionDTO.from(lesson, exercises, progress);
    }

    public ProgressSummaryDTO submitAnswer(String userId, String lessonId, ExerciseResultRequest request) {
        Lesson lesson = findLesson(lessonId);
        List<Exercise> exercises = exerciseRepository.findByLessonIdOrderByOrderNumberAsc(lessonId);
        if (exercises.isEmpty()) {
            throw new IllegalArgumentException("Lesson has no exercises: " + lessonId);
        }

        UserProgress progress = userProgressRepository.findByUserIdAndLessonId(userId, lessonId)
                .orElseGet(() -> createInitialProgress(userId, lesson));

        if (!progress.getIsUnlocked()) {
            throw new IllegalArgumentException("Lesson is locked for user: " + userId);
        }

        Exercise exercise = exercises.stream()
                .filter(item -> item.getId().equals(request.exerciseId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Exercise not found in lesson: " + request.exerciseId()));

        validateExerciseSchema(exercise);

        if (progress.getIsCompleted()) {
            return ProgressSummaryDTO.from(progress, exercises.size());
        }

        boolean answerCorrect = isAnswerCorrect(exercise, request.submittedAnswer());
        int awardedPoints = answerCorrect ? exercise.getMaxPoints() : 0;
        progress.setTotalScore(progress.getTotalScore() + awardedPoints);

        int nextIndex = Math.max(progress.getCurrentExerciseIndex(), exercise.getOrderNumber());
        progress.setCurrentExerciseIndex(nextIndex);

        if (nextIndex >= exercises.size()) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
        }

        UserProgress savedProgress = userProgressRepository.save(progress);
        unlockNextLessonIfNeeded(userId, lesson, savedProgress.getIsCompleted());

        return ProgressSummaryDTO.from(savedProgress, exercises.size(), answerCorrect, awardedPoints);
    }

    @Transactional(readOnly = true)
    public List<ProgressSummaryDTO> getUserProgress(String userId) {
        return userProgressRepository.findByUserId(userId).stream()
                .map(progress -> ProgressSummaryDTO.from(progress, exerciseRepository.countByLessonId(progress.getLessonId())))
                .toList();
    }

    private boolean isAnswerCorrect(Exercise exercise, String submittedAnswer) {
        try {
            JsonNode expected = objectMapper.readTree(exercise.getCorrectAnswer());
            JsonNode submitted = readSubmittedAnswer(submittedAnswer);

            return switch (exercise.getType()) {
                case MULTIPLE_CHOICE, NOTE_IDENTIFICATION -> {
                    requireField(expected, "answer", exercise.getId());
                    yield normalizeValue(expected.get("answer")).equals(normalizeValue(submitted));
                }
                case RHYTHM_READING -> {
                    requireField(expected, "answers", exercise.getId());
                    yield normalizeArray(expected.get("answers")).equals(normalizeArray(submitted));
                }
            };
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Exercise answer payload is invalid for exercise: " + exercise.getId());
        }
    }

    private void validateExerciseSchema(Exercise exercise) {
        try {
            JsonNode questionData = objectMapper.readTree(exercise.getQuestionData());
            JsonNode correctAnswer = objectMapper.readTree(exercise.getCorrectAnswer());

            switch (exercise.getType()) {
                case MULTIPLE_CHOICE -> {
                    requireField(questionData, "question", exercise.getId());
                    requireArrayField(questionData, "options", exercise.getId());
                    requireField(correctAnswer, "answer", exercise.getId());
                }
                case NOTE_IDENTIFICATION -> {
                    requireField(questionData, "question", exercise.getId());
                    requireField(questionData, "staff", exercise.getId());
                    requireField(questionData, "highlightedNote", exercise.getId());
                    requireField(correctAnswer, "answer", exercise.getId());
                }
                case RHYTHM_READING -> {
                    requireField(questionData, "question", exercise.getId());
                    requireArrayField(questionData, "pattern", exercise.getId());
                    requireArrayField(correctAnswer, "answers", exercise.getId());
                }
            }
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Exercise JSON is invalid for exercise: " + exercise.getId());
        }
    }

    private JsonNode readSubmittedAnswer(String submittedAnswer) throws JsonProcessingException {
        String trimmed = submittedAnswer.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return objectMapper.readTree(trimmed);
        }
        return objectMapper.getNodeFactory().textNode(trimmed);
    }

    private void requireField(JsonNode node, String fieldName, String exerciseId) {
        if (node == null || !node.hasNonNull(fieldName)) {
            throw new IllegalArgumentException("Missing field '" + fieldName + "' for exercise: " + exerciseId);
        }
    }

    private void requireArrayField(JsonNode node, String fieldName, String exerciseId) {
        requireField(node, fieldName, exerciseId);
        if (!node.get(fieldName).isArray() || node.get(fieldName).isEmpty()) {
            throw new IllegalArgumentException("Field '" + fieldName + "' must be a non-empty array for exercise: " + exerciseId);
        }
    }

    private String normalizeValue(JsonNode node) {
        JsonNode normalized = normalizeNode(node);
        return normalized.isTextual() ? normalized.asText() : normalized.toString();
    }

    private List<String> normalizeArray(JsonNode node) {
        JsonNode source = node != null && node.isArray() ? node : objectMapper.createArrayNode().add(normalizeValue(node));
        List<String> values = new ArrayList<>();
        source.forEach(item -> values.add(normalizeValue(item)));
        values.sort(Comparator.naturalOrder());
        return values;
    }

    private JsonNode normalizeNode(JsonNode node) {
        if (node == null || node.isNull()) {
            return objectMapper.getNodeFactory().nullNode();
        }
        if (node.isTextual()) {
            return objectMapper.getNodeFactory().textNode(node.asText().trim().toLowerCase());
        }
        if (node.isArray()) {
            var arrayNode = objectMapper.createArrayNode();
            node.forEach(item -> arrayNode.add(normalizeNode(item)));
            return arrayNode;
        }
        if (node.isObject()) {
            var objectNode = objectMapper.createObjectNode();
            var fieldNames = new ArrayList<String>();
            node.fieldNames().forEachRemaining(fieldNames::add);
            fieldNames.sort(Comparator.naturalOrder());
            fieldNames.forEach(field -> objectNode.set(field, normalizeNode(node.get(field))));
            return objectNode;
        }
        return node;
    }

    private UserProgress createInitialProgress(String userId, Lesson lesson) {
        boolean isFirstLessonOfLevel = lesson.getOrderNumber() != null && lesson.getOrderNumber() == 1;

        UserProgress progress = UserProgress.builder()
                .userId(userId)
                .lessonId(lesson.getId())
                .isUnlocked(isFirstLessonOfLevel)
                .currentExerciseIndex(0)
                .totalScore(0)
                .isCompleted(false)
                .build();

        return userProgressRepository.save(progress);
    }

    private void unlockNextLessonIfNeeded(String userId, Lesson lesson, boolean lessonCompleted) {
        if (!lessonCompleted) {
            return;
        }

        List<Lesson> lessonsInLevel = lessonRepository.findByLevelIdOrderByOrderNumberAsc(lesson.getLevelId());
        lessonsInLevel.stream()
                .filter(item -> item.getOrderNumber() != null && lesson.getOrderNumber() != null)
                .filter(item -> item.getOrderNumber().equals(lesson.getOrderNumber() + 1))
                .findFirst()
                .ifPresent(nextLesson -> {
                    UserProgress nextProgress = userProgressRepository.findByUserIdAndLessonId(userId, nextLesson.getId())
                            .orElseGet(() -> UserProgress.builder()
                                    .userId(userId)
                                    .lessonId(nextLesson.getId())
                                    .currentExerciseIndex(0)
                                    .totalScore(0)
                                    .isCompleted(false)
                                    .build());

                    nextProgress.setIsUnlocked(true);
                    userProgressRepository.save(nextProgress);
                });
    }

    private Lesson findLesson(String lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with id: " + lessonId));
    }
}