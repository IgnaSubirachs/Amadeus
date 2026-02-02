package com.amadeus.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad User - Representa un usuario de la aplicación Amadeus
 * 
 * SOLID Principles aplicados:
 * - Single Responsibility: Solo representa datos de usuario
 * - Open/Closed: Extensible mediante herencia si fuera necesario
 * 
 * Anotaciones JPA:
 * 
 * @Entity - Marca esta clase como entidad de base de datos
 * @Table - Especifica el nombre de la tabla
 * 
 *        Anotaciones Lombok:
 * @Data - Genera getters, setters, toString, equals, hashCode
 * @Builder - Patrón Builder para construcción de objetos
 * @NoArgsConstructor - Constructor sin argumentos (requerido por JPA)
 * @AllArgsConstructor - Constructor con todos los argumentos (para Builder)
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Identificador único del usuario
     * 
     * @GeneratedValue con UUID generará IDs automáticamente
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Email del usuario - único en la base de datos
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Nombre de usuario visible
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * Contraseña hasheada (NUNCA guardar en texto plano)
     * Usaremos BCrypt para el hash
     */
    @Column(nullable = false, name = "password_hash")
    private String passwordHash;

    /**
     * Fecha de creación del usuario
     */
    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Última fecha de login
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * URL o path de la imagen de perfil (opcional)
     */
    @Column(name = "profile_image")
    private String profileImage;

    /**
     * Método del ciclo de vida JPA
     * Se ejecuta automáticamente antes de persistir en BD
     * Establece createdAt si no está definido
     */
    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * Método del ciclo de vida JPA
     * Se ejecuta antes de actualizar en BD
     */
    @PreUpdate
    protected void preUpdate() {
        // Podríamos agregar lógica de auditoría aquí
    }

    /**
     * Override de equals para comparar usuarios por ID
     * Importante para colecciones y tests
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    /**
     * Override de hashCode consistente con equals
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * ToString personalizado para evitar mostrar el password hash
     */
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
