package com.amadeus.domain.service;

import com.amadeus.domain.model.UserProgress;
import com.amadeus.domain.repository.UserProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProgressService {

    private final UserProgressRepository userProgressRepository;

    public UserProgress recordProgress(UserProgress progress) {
        Optional<UserProgress> existingProgress = userProgressRepository
                .findByUserIdAndLessonId(progress.getUserId(), progress.getLessonId());

        if (existingProgress.isPresent()) {
            UserProgress current = existingProgress.get();
            // Update only if the new score is higher or it's newly completed
            if (progress.getScore() > current.getScore()) {
                current.setScore(progress.getScore());
            }
            if (progress.getCurrentExerciseIndex() != null
                    && progress.getCurrentExerciseIndex() > current.getCurrentExerciseIndex()) {
                current.setCurrentExerciseIndex(progress.getCurrentExerciseIndex());
            }
            if (progress.getIsCompleted() && !current.getIsCompleted()) {
                current.setIsCompleted(true);
                current.setCompletedAt(LocalDateTime.now());
            }
            return userProgressRepository.save(current);
        }

        if (progress.getIsCompleted() && progress.getCompletedAt() == null) {
            progress.setCompletedAt(LocalDateTime.now());
        }

        return userProgressRepository.save(progress);
    }

    @Transactional(readOnly = true)
    public List<UserProgress> getUserProgress(String userId) {
        return userProgressRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public Optional<UserProgress> getProgressForLesson(String userId, String lessonId) {
        return userProgressRepository.findByUserIdAndLessonId(userId, lessonId);
    }

    @Transactional(readOnly = true)
    public List<UserProgress> getCompletedLessons(String userId) {
        return userProgressRepository.findByUserIdAndIsCompletedTrue(userId);
    }

    @Transactional(readOnly = true)
    public long getCompletedLessonsCount(String userId) {
        return userProgressRepository.countByUserIdAndIsCompletedTrue(userId);
    }
}
