package com.amadeus.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para recibir peticiones de creación de usuario
 * 
 * ¿Por qué separar Request de Response?
 * - Request: Lo que recibe la API (incluye password)
 * - Response (UserDTO): Lo que devuelve la API (NO incluye password)
 * 
 * Validaciones Bean Validation (Jakarta):
 * 
 * @NotBlank - No puede ser null, vacío o solo espacios
 * @Email - Formato de email válido
 * @Size - Longitud mínima/máxima
 * 
 *       Spring Boot valida automáticamente con @Valid en el Controller
 */
public record CreateUserRequest(

        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") @Size(max = 100, message = "Email must be less than 100 characters") String email,

        @NotBlank(message = "Username is required") @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters") String username,

        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password) {
    // El password se enviará en texto plano desde el cliente
    // pero se hasheará ANTES de guardar en BD (en el Service)
}
