package com.amadeus.domain.repository;

import com.amadeus.domain.model.UserProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, String> {

    List<UserProgress> findByUserId(String userId);

    Optional<UserProgress> findByUserIdAndLessonId(String userId, String lessonId);

    List<UserProgress> findByUserIdAndIsCompletedTrue(String userId);

    long countByUserIdAndIsCompletedTrue(String userId);
}
