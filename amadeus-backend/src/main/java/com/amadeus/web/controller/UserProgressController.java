package com.amadeus.web.controller;

import com.amadeus.domain.model.UserProgress;
import com.amadeus.domain.service.UserProgressService;
import com.amadeus.web.dto.RecordProgressRequest;
import com.amadeus.web.dto.UserProgressDTO;
import com.amadeus.web.mapper.UserProgressMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProgressController {

    private final UserProgressService userProgressService;

    @PostMapping
    public ResponseEntity<UserProgressDTO> recordProgress(@RequestBody RecordProgressRequest request) {
        UserProgress progress = UserProgressMapper.toEntity(request);
        UserProgress recordedProgress = userProgressService.recordProgress(progress);
        return ResponseEntity.ok(UserProgressMapper.toDTO(recordedProgress));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserProgressDTO>> getUserProgress(@PathVariable String userId) {
        List<UserProgressDTO> progressList = userProgressService.getUserProgress(userId).stream()
                .map(UserProgressMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/user/{userId}/lesson/{lessonId}")
    public ResponseEntity<UserProgressDTO> getProgressForLesson(
            @PathVariable String userId, @PathVariable String lessonId) {
        return userProgressService.getProgressForLesson(userId, lessonId)
                .map(UserProgressMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<UserProgressDTO>> getCompletedLessons(@PathVariable String userId) {
        List<UserProgressDTO> completed = userProgressService.getCompletedLessons(userId).stream()
                .map(UserProgressMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(completed);
    }

    @GetMapping("/user/{userId}/completed/count")
    public ResponseEntity<Long> getCompletedLessonsCount(@PathVariable String userId) {
        long count = userProgressService.getCompletedLessonsCount(userId);
        return ResponseEntity.ok(count);
    }
}
