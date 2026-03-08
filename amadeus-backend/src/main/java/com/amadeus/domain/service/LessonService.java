package com.amadeus.domain.service;

import com.amadeus.domain.model.Lesson;
import com.amadeus.domain.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;

    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    @Transactional(readOnly = true)
    public List<Lesson> getLessonsByLevel(String levelId) {
        return lessonRepository.findByLevelIdOrderByOrderNumberAsc(levelId);
    }

    @Transactional(readOnly = true)
    public Lesson getLessonById(String id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lección no encontrada: " + id));
    }

    public Lesson updateLesson(String id, Lesson lessonDetails) {
        Lesson lesson = getLessonById(id);

        lesson.setTitle(lessonDetails.getTitle());
        lesson.setDescription(lessonDetails.getDescription());
        lesson.setContent(lessonDetails.getContent());
        lesson.setMusicTheoryTopic(lessonDetails.getMusicTheoryTopic());
        lesson.setOrderNumber(lessonDetails.getOrderNumber());

        return lessonRepository.save(lesson);
    }

    public void deleteLesson(String id) {
        Lesson lesson = getLessonById(id);
        lessonRepository.delete(lesson);
    }
}
