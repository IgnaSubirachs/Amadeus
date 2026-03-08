package com.amadeus.web.controller;

import com.amadeus.domain.service.LessonSessionService;
import com.amadeus.web.dto.ExerciseResultRequest;
import com.amadeus.web.dto.LessonSessionDTO;
import com.amadeus.web.dto.ProgressSummaryDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/lessons")
@RequiredArgsConstructor
public class LessonSessionController {

    private final LessonSessionService lessonSessionService;

    @PostMapping("/{lessonId}/start")
    public ResponseEntity<LessonSessionDTO> startLesson(
            @PathVariable String userId,
            @PathVariable String lessonId) {
        return ResponseEntity.ok(lessonSessionService.startLesson(userId, lessonId));
    }

    @PostMapping("/{lessonId}/answer")
    public ResponseEntity<ProgressSummaryDTO> submitAnswer(
            @PathVariable String userId,
            @PathVariable String lessonId,
            @Valid @RequestBody ExerciseResultRequest request) {
        return ResponseEntity.ok(lessonSessionService.submitAnswer(userId, lessonId, request));
    }

    @GetMapping("/progress")
    public ResponseEntity<List<ProgressSummaryDTO>> getProgress(@PathVariable String userId) {
        return ResponseEntity.ok(lessonSessionService.getUserProgress(userId));
    }
}