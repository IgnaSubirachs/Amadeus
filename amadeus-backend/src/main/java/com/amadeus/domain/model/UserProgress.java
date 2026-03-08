package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad UserProgress - Rastrea el progreso del usuario en cada leccion.
 */
@Entity
@Table(name = "user_progress", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_progress_user_lesson", columnNames = { "user_id", "lesson_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "user_id")
    private String userId;

    @Column(nullable = false, name = "lesson_id")
    private String lessonId;

    @Column(nullable = false, name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "current_exercise_index")
    @Builder.Default
    private Integer currentExerciseIndex = 0;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(nullable = false, name = "is_unlocked")
    @Builder.Default
    private Boolean isUnlocked = false;

    @Column(name = "total_score")
    @Builder.Default
    private Integer totalScore = 0;

    @PrePersist
    protected void prePersist() {
        if (currentExerciseIndex == null) {
            currentExerciseIndex = 0;
        }
        if (totalScore == null) {
            totalScore = 0;
        }
        if (isUnlocked == null) {
            isUnlocked = false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserProgress that = (UserProgress) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}