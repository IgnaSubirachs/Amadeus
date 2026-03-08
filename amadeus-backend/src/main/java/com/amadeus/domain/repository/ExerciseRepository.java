package com.amadeus.domain.repository;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.ExerciseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, String> {

    List<Exercise> findByLessonIdOrderByOrderNumberAsc(String lessonId);

    List<Exercise> findByType(ExerciseType type);

    long countByLessonId(String lessonId);
}