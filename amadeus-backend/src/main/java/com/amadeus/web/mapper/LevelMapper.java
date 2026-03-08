package com.amadeus.web.mapper;

import com.amadeus.domain.model.Level;
import com.amadeus.web.dto.CreateLevelRequest;
import com.amadeus.web.dto.LevelDTO;

public class LevelMapper {

    public static Level toEntity(CreateLevelRequest request) {
        if (request == null)
            return null;

        Level level = new Level();
        level.setName(request.name());
        level.setDescription(request.description());
        level.setOrderNumber(request.orderNumber());
        level.setImageUrl(request.imageUrl());

        return level;
    }

    public static LevelDTO toDTO(Level level) {
        if (level == null)
            return null;

        return new LevelDTO(
                level.getId(),
                level.getName(),
                level.getDescription(),
                level.getOrderNumber(),
                level.getImageUrl());
    }
}
