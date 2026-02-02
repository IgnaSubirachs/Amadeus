package com.amadeus.domain.repository;

import com.amadeus.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad User
 * 
 * SOLID Principles:
 * - Interface Segregation: Solo métodos relacionados con User
 * - Dependency Inversion: Dependemos de la interfaz, no de implementación
 * 
 * Spring Data JPA genera automáticamente la implementación de:
 * - save(User) - Guardar o actualizar
 * - findById(String) - Buscar por ID
 * - findAll() - Obtener todos
 * - delete(User) - Eliminar
 * - count() - Contar
 * 
 * Métodos custom que definimos:
 * Spring Data JPA los implementa automáticamente usando el nombre del método
 * - findByEmail: SELECT * FROM users WHERE email = ?
 * - existsByEmail: SELECT COUNT(*) > 0 FROM users WHERE email = ?
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * Busca un usuario por email
     * 
     * @param email Email del usuario
     * @return Optional con el usuario si existe, vacío si no
     */
    Optional<User> findByEmail(String email);

    /**
     * Verifica si existe un usuario con ese email
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);

    /**
     * Busca un usuario por username
     * 
     * @param username Nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<User> findByUsername(String username);

    /**
     * Verifica si existe un usuario con ese username
     * 
     * @param username Username a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);
}
