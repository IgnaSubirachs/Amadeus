package com.amadeus.web.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para exponer información de Usuario
 * 
 * ¿Por qué usar Record?
 * - Inmutable (thread-safe)
 * - Conciso (sin boilerplate)
 * - Automático: equals, hashCode, toString, getters
 * - Ideal para transferencia de datos
 * 
 * ¿Por qué NO exponer la entidad User directamente?
 * - Seguridad: NO exponemos passwordHash
 * - Desacoplamiento: La estructura de la BD puede cambiar sin afectar la API
 * - Control: Solo exponemos los campos que queremos
 * 
 * Anotaciones JSON (Jackson):
 * Spring Boot serializa esto automáticamente a JSON
 */
public record UserDTO(
        String id,
        String email,
        String username,
        String profileImage,
        LocalDateTime createdAt) {
    // Records pueden tener métodos adicionales si es necesario

    /**
     * Constructor compacto para validación (opcional)
     * Se ejecuta ANTES del constructor canónico
     */
    public UserDTO {
        // Aquí podrías validar si fuera necesario
        // Por ejemplo: Objects.requireNonNull(email, "Email cannot be null");
    }
}
