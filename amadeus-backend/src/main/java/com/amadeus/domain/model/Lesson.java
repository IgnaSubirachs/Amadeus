package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

/**
 * Entidad Lesson - Representa una lección sobre un tema específico
 * 
 * Cada lección pertenece a un Level y contiene múltiples Exercises
 * Ejemplo: "La Clave de Sol", "Lectura de Notas en Do Mayor"
 */
@Entity
@Table(name = "lessons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Relación con Level
     * Una lección pertenece a un nivel
     */
    @Column(nullable = false, name = "level_id")
    private String levelId;

    /**
     * Orden dentro del nivel (1, 2, 3...)
     */
    @Column(nullable = false, name = "order_number")
    private Integer orderNumber;

    /**
     * Título de la lección
     */
    @Column(nullable = false, length = 150)
    private String title;

    /**
     * Descripción de qué se aprenderá
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Contenido completo de la lección (texto/html/markdown según front-end)
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Tema musical específico
     * Ejemplos: "clefs", "notes", "rhythms", "intervals"
     */
    @Column(nullable = false, name = "music_theory_topic", length = 50)
    private String musicTheoryTopic;

    /**
     * Tiempo estimado en minutos para completar
     */
    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Lesson lesson = (Lesson) o;
        return Objects.equals(id, lesson.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
