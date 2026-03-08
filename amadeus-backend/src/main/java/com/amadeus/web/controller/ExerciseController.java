package com.amadeus.web.controller;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.service.ExerciseService;
import com.amadeus.web.dto.CreateExerciseRequest;
import com.amadeus.web.dto.ExerciseDTO;
import com.amadeus.web.mapper.ExerciseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExerciseController {

    private final ExerciseService exerciseService;

    @PostMapping
    public ResponseEntity<ExerciseDTO> createExercise(@RequestBody CreateExerciseRequest request) {
        Exercise exercise = ExerciseMapper.toEntity(request);
        Exercise createdExercise = exerciseService.createExercise(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExerciseMapper.toDTO(createdExercise));
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<List<ExerciseDTO>> getExercisesByLesson(@PathVariable String lessonId) {
        List<ExerciseDTO> exercises = exerciseService.getExercisesByLesson(lessonId).stream()
                .map(ExerciseMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(exercises);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable String id) {
        Exercise exercise = exerciseService.getExerciseById(id);
        return ResponseEntity.ok(ExerciseMapper.toDTO(exercise));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExerciseDTO> updateExercise(@PathVariable String id,
            @RequestBody CreateExerciseRequest request) {
        Exercise updatedExercise = exerciseService.updateExercise(id, ExerciseMapper.toEntity(request));
        return ResponseEntity.ok(ExerciseMapper.toDTO(updatedExercise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable String id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.noContent().build();
    }
}
