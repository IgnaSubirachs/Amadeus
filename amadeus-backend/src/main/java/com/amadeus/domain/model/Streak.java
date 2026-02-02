package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad Streak - Racha de días consecutivos practicando
 * 
 * Relación 1:1 con User (cada usuario tiene una racha)
 * Gamificación tipo Duolingo
 */
@Entity
@Table(name = "streaks")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Streak {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, name = "user_id")
    private String userId;

    /**
     * Días consecutivos actuales
     */
    @Column(nullable = false, name = "current_streak")
    @Builder.Default
    private Integer currentStreak = 0;

    /**
     * Mejor racha histórica
     */
    @Column(nullable = false, name = "longest_streak")
    @Builder.Default
    private Integer longestStreak = 0;

    /**
     * Última fecha de práctica
     */
    @Column(name = "last_practice_date")
    private LocalDateTime lastPracticeDate;

    @PrePersist
    protected void prePersist() {
        if (currentStreak == null) {
            currentStreak = 0;
        }
        if (longestStreak == null) {
            longestStreak = 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Streak streak = (Streak) o;
        return Objects.equals(id, streak.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
