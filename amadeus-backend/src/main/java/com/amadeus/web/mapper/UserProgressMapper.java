package com.amadeus.web.mapper;

import com.amadeus.domain.model.UserProgress;
import com.amadeus.web.dto.RecordProgressRequest;
import com.amadeus.web.dto.UserProgressDTO;

public class UserProgressMapper {

    public static UserProgress toEntity(RecordProgressRequest request) {
        if (request == null)
            return null;

        UserProgress progress = new UserProgress();
        progress.setUserId(request.userId());
        progress.setLessonId(request.lessonId());
        progress.setScore(request.score() != null ? request.score() : 0);
        progress.setIsCompleted(request.isCompleted() != null ? request.isCompleted() : false);

        return progress;
    }

    public static UserProgressDTO toDTO(UserProgress progress) {
        if (progress == null)
            return null;

        return new UserProgressDTO(
                progress.getId(),
                progress.getUserId(),
                progress.getLessonId(),
                progress.getScore(),
                progress.getIsCompleted(),
                progress.getCompletedAt());
    }
}
