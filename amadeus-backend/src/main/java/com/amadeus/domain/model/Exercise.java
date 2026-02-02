package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

/**
 * Entidad Exercise - Representa un ejercicio individual
 * 
 * Cada ejercicio pertenece a una Lesson
 * Tipos: multipleChoice, noteIdentification, rhythmReading, etc.
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

    /**
     * Relación con Lesson
     */
    @Column(nullable = false, name = "lesson_id")
    private String lessonId;

    /**
     * Tipo de ejercicio
     * Ejemplos: "multipleChoice", "noteIdentification", "rhythmReading"
     */
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Dificultad específica del ejercicio
     */
    @Column(length = 20)
    private String difficulty;

    /**
     * Orden dentro de la lección
     */
    @Column(nullable = false, name = "order_number")
    private Integer orderNumber;

    /**
     * Datos de la pregunta en formato JSON
     * Flexible para soportar diferentes tipos de ejercicios
     * 
     * Ejemplo para multipleChoice:
     * {
     * "question": "¿Qué nota es esta?",
     * "options": ["Do", "Re", "Mi", "Fa"],
     * "imageUrl": "https://..."
     * }
     */
    @Column(nullable = false, name = "question_data", columnDefinition = "TEXT")
    private String questionData;

    /**
     * Respuesta correcta en formato JSON
     * Ejemplo: {"answer": "Do"} o {"answers": ["Do", "Mi"]}
     */
    @Column(nullable = false, name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    /**
     * Puntos máximos por respuesta correcta
     */
    @Column(nullable = false, name = "max_points")
    @Builder.Default
    private Integer maxPoints = 10;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(id, exercise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
