package com.amadeus.domain.service;

import com.amadeus.domain.model.Level;
import com.amadeus.domain.repository.LevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LevelService {

    private final LevelRepository levelRepository;

    public Level createLevel(Level level) {
        if (levelRepository.existsByOrderNumber(level.getOrderNumber())) {
            throw new IllegalArgumentException("Ya existe un nivel con el orden: " + level.getOrderNumber());
        }
        return levelRepository.save(level);
    }

    @Transactional(readOnly = true)
    public List<Level> getAllLevels() {
        return levelRepository.findAllByOrderByOrderNumberAsc();
    }

    @Transactional(readOnly = true)
    public Level getLevelById(String id) {
        return levelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nivel no encontrado: " + id));
    }

    public Level updateLevel(String id, Level levelDetails) {
        Level level = getLevelById(id);

        if (!level.getOrderNumber().equals(levelDetails.getOrderNumber()) &&
                levelRepository.existsByOrderNumber(levelDetails.getOrderNumber())) {
            throw new IllegalArgumentException("Ya existe otro nivel con el orden: " + levelDetails.getOrderNumber());
        }

        level.setName(levelDetails.getName());
        level.setDescription(levelDetails.getDescription());
        level.setOrderNumber(levelDetails.getOrderNumber());
        level.setImageUrl(levelDetails.getImageUrl());

        return levelRepository.save(level);
    }

    public void deleteLevel(String id) {
        Level level = getLevelById(id);
        levelRepository.delete(level);
    }
}
