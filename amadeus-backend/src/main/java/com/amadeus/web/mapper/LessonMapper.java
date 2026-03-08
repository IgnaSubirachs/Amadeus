package com.amadeus.web.mapper;

import com.amadeus.domain.model.Lesson;
import com.amadeus.web.dto.CreateLessonRequest;
import com.amadeus.web.dto.LessonDTO;

public class LessonMapper {

    public static Lesson toEntity(CreateLessonRequest request) {
        if (request == null)
            return null;

        Lesson lesson = new Lesson();
        lesson.setLevelId(request.levelId());
        lesson.setTitle(request.title());
        lesson.setDescription(request.description());
        lesson.setContent(request.content());
        lesson.setMusicTheoryTopic(request.musicTheoryTopic());
        lesson.setOrderNumber(request.orderNumber());

        return lesson;
    }

    public static LessonDTO toDTO(Lesson lesson) {
        if (lesson == null)
            return null;

        return new LessonDTO(
                lesson.getId(),
                lesson.getLevelId(),
                lesson.getTitle(),
                lesson.getDescription(),
                lesson.getContent(),
                lesson.getMusicTheoryTopic(),
                lesson.getOrderNumber());
    }
}
