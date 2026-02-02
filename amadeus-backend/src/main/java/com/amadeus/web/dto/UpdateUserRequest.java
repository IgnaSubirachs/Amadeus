package com.amadeus.web.dto;

import jakarta.validation.constraints.Size;

/**
 * DTO para actualizar el perfil de usuario
 * 
 * ¿Por qué campos opcionales (nullable)?
 * - Permite actualización parcial (PATCH)
 * - Solo se actualizan los campos enviados
 * - Ejemplo: Solo cambiar username sin tocar profileImage
 * 
 * Limitaciones:
 * - NO permite cambiar email (identificador único)
 * - NO permite cambiar password (debe ser endpoint separado por seguridad)
 */
public record UpdateUserRequest(

        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username,

        @Size(max = 255, message = "Profile image URL must be less than 255 characters") String profileImage) {
    // Ambos campos son opcionales (pueden ser null)
    // El Service verificará qué campos actualizar
}
