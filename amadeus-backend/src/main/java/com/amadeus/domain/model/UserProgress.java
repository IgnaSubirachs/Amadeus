package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad UserProgress - Rastrea el progreso del usuario en cada lección
 * 
 * Relación: Un User tiene múltiples UserProgress (uno por lección)
 */
@Entity
@Table(name = "user_progress")
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

    /**
     * Si la lección está completada
     */
    @Column(nullable = false, name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = false;

    /**
     * Índice del ejercicio actual (si no ha completado)
     * Permite retomar donde dejó
     */
    @Column(name = "current_exercise_index")
    @Builder.Default
    private Integer currentExerciseIndex = 0;

    /**
     * Fecha de completado (null si no ha terminado)
     */
    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Puntuación total en la lección
     */
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserProgress that = (UserProgress) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
