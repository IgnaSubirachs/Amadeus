package com.amadeus.web.mapper;

import com.amadeus.domain.model.User;
import com.amadeus.web.dto.CreateUserRequest;
import com.amadeus.web.dto.UpdateUserRequest;
import com.amadeus.web.dto.UserDTO;

/**
 * Mapper para convertir entre Entity (User) y DTOs
 * 
 * SOLID Principles:
 * - Single Responsibility: Solo mapea/convierte objetos
 * - Open/Closed: Extensible para nuevas conversiones
 * 
 * ¿Por qué métodos estáticos?
 * - Mapper es stateless (sin estado)
 * - No necesita instancias
 * - Uso simple: UserMapper.toDTO(user)
 * 
 * Alternativas más avanzadas:
 * - MapStruct (generación automática de mappers)
 * - ModelMapper (mapping por reflexión)
 * Para aprender, hacemos mapping manual (más didáctico)
 */
public class UserMapper {

    // Constructor privado para evitar instanciación
    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Convierte Entity User a DTO UserDTO
     * Usado para respuestas de API (NO expone passwordHash)
     * 
     * @param user Entidad de dominio
     * @return DTO para respuesta HTTP
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getProfileImage(),
                user.getCreatedAt());
    }

    /**
     * Convierte CreateUserRequest a Entity User
     * 
     * IMPORTANTE: El password ya debe venir hasheado
     * La responsabilidad de hashear es del Service, no del Mapper
     * 
     * @param request        Petición de creación
     * @param hashedPassword Password ya hasheado (BCrypt)
     * @return Entity User lista para persistir
     */
    public static User toEntity(CreateUserRequest request, String hashedPassword) {
        if (request == null) {
            return null;
        }

        return User.builder()
                .email(request.email())
                .username(request.username())
                .passwordHash(hashedPassword)
                .build();
        // createdAt se establece automáticamente con @PrePersist
    }

    /**
     * Actualiza una entidad existente con datos de UpdateUserRequest
     * 
     * ¿Por qué modificar la entidad en lugar de crear una nueva?
     * - JPA rastrea cambios en entidades gestionadas
     * - Solo actualiza campos modificados (eficiente)
     * - Mantiene el mismo ID y datos no modificables
     * 
     * @param user    Entidad existente a actualizar
     * @param request Datos de actualización (campos opcionales)
     */
    public static void updateEntity(User user, UpdateUserRequest request) {
        if (user == null || request == null) {
            return;
        }

        // Solo actualiza si el campo NO es null
        if (request.username() != null) {
            user.setUsername(request.username());
        }

        if (request.profileImage() != null) {
            user.setProfileImage(request.profileImage());
        }

        // Email NO se actualiza (identificador único)
        // Password NO se actualiza (requiere endpoint separado)
    }
}
