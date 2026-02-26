package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Objects;

/**
 * Entidad Level - Representa un nivel de dificultad en la app
 * 
 * Organiza las lecciones de forma progresiva
 * Similar a los "mundos" en Duolingo
 * 
 * Ejemplo: Nivel 1: Notas Básicas, Nivel 2: Ritmos, etc.
 */
@Entity
@Table(name = "levels")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Level {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Orden de aparición del nivel (1, 2, 3...)
     * Determina la secuencia de aprendizaje
     */
    @Column(nullable = false, unique = true, name = "order_number")
    private Integer orderNumber;

    /**
     * Nombre del nivel (ej: "Nivel 1: Notas Básicas")
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * Descripción del nivel
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Dificultad: beginner, intermediate, advanced
     */
    @Column(nullable = false, length = 20)
    private String difficulty;

    /**
     * Si está bloqueado hasta completar el anterior
     */
    @Column(nullable = false, name = "is_locked")
    @Builder.Default
    private Boolean isLocked = true;

    /**
     * URL o path de la imagen representativa del nivel
     */
    @Column(name = "image_url")
    private String imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Level level = (Level) o;
        return Objects.equals(id, level.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
