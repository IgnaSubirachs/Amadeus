package com.amadeus.domain.service;

import com.amadeus.domain.repository.ExerciseRepository;
import com.amadeus.domain.repository.LessonRepository;
import com.amadeus.domain.repository.LevelRepository;
import com.amadeus.web.dto.CourseTreeDTO;
import com.amadeus.web.dto.LessonTreeDTO;
import com.amadeus.web.dto.LevelTreeDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogService {

    private final LevelRepository levelRepository;
    private final LessonRepository lessonRepository;
    private final ExerciseRepository exerciseRepository;

    public CourseTreeDTO getCourseTree() {
        return new CourseTreeDTO(levelRepository.findAllByOrderByOrderNumberAsc().stream()
                .map(level -> new LevelTreeDTO(
                        level.getId(),
                        level.getOrderNumber(),
                        level.getName(),
                        level.getDescription(),
                        level.getDifficulty(),
                        lessonRepository.findByLevelIdOrderByOrderNumberAsc(level.getId()).stream()
                                .map(lesson -> new LessonTreeDTO(
                                        lesson.getId(),
                                        lesson.getOrderNumber(),
                                        lesson.getTitle(),
                                        lesson.getDescription(),
                                        lesson.getMusicTheoryTopic(),
                                        lesson.getEstimatedMinutes(),
                                        exerciseRepository.countByLessonId(lesson.getId())))
                                .toList()))
                .toList());
    }
}