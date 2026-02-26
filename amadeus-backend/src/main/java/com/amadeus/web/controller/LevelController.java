package com.amadeus.web.controller;

import com.amadeus.domain.model.Level;
import com.amadeus.domain.service.LevelService;
import com.amadeus.web.dto.CreateLevelRequest;
import com.amadeus.web.dto.LevelDTO;
import com.amadeus.web.mapper.LevelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/levels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LevelController {

    private final LevelService levelService;

    @PostMapping
    public ResponseEntity<LevelDTO> createLevel(@RequestBody CreateLevelRequest request) {
        Level level = LevelMapper.toEntity(request);
        Level createdLevel = levelService.createLevel(level);
        return ResponseEntity.status(HttpStatus.CREATED).body(LevelMapper.toDTO(createdLevel));
    }

    @GetMapping
    public ResponseEntity<List<LevelDTO>> getAllLevels() {
        List<LevelDTO> levels = levelService.getAllLevels().stream()
                .map(LevelMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(levels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LevelDTO> getLevelById(@PathVariable String id) {
        Level level = levelService.getLevelById(id);
        return ResponseEntity.ok(LevelMapper.toDTO(level));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LevelDTO> updateLevel(@PathVariable String id, @RequestBody CreateLevelRequest request) {
        Level updatedLevel = levelService.updateLevel(id, LevelMapper.toEntity(request));
        return ResponseEntity.ok(LevelMapper.toDTO(updatedLevel));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLevel(@PathVariable String id) {
        levelService.deleteLevel(id);
        return ResponseEntity.noContent().build();
    }
}
