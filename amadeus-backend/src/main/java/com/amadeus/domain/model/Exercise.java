package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Entidad Exercise - Representa un ejercicio individual.
 */
@Entity
@Table(name = "exercises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, name = "lesson_id")
    private String lessonId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ExerciseType type;

    @Column(length = 20)
    private String difficulty;

    @Column(nullable = false, name = "order_number")
    private Integer orderNumber;

    @Column(nullable = false, name = "question_data", columnDefinition = "TEXT")
    private String questionData;

    @Column(nullable = false, name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(nullable = false, name = "max_points")
    @Builder.Default
    private Integer maxPoints = 10;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Exercise exercise = (Exercise) o;
        return Objects.equals(id, exercise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}