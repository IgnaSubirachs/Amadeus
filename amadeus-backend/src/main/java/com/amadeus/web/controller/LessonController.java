package com.amadeus.web.controller;

import com.amadeus.domain.model.Lesson;
import com.amadeus.domain.service.LessonService;
import com.amadeus.web.dto.CreateLessonRequest;
import com.amadeus.web.dto.LessonDTO;
import com.amadeus.web.mapper.LessonMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<LessonDTO> createLesson(@RequestBody CreateLessonRequest request) {
        Lesson lesson = LessonMapper.toEntity(request);
        Lesson createdLesson = lessonService.createLesson(lesson);
        return ResponseEntity.status(HttpStatus.CREATED).body(LessonMapper.toDTO(createdLesson));
    }

    @GetMapping("/level/{levelId}")
    public ResponseEntity<List<LessonDTO>> getLessonsByLevel(@PathVariable String levelId) {
        List<LessonDTO> lessons = lessonService.getLessonsByLevel(levelId).stream()
                .map(LessonMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable String id) {
        Lesson lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(LessonMapper.toDTO(lesson));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LessonDTO> updateLesson(@PathVariable String id, @RequestBody CreateLessonRequest request) {
        Lesson updatedLesson = lessonService.updateLesson(id, LessonMapper.toEntity(request));
        return ResponseEntity.ok(LessonMapper.toDTO(updatedLesson));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLesson(@PathVariable String id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.noContent().build();
    }
}
