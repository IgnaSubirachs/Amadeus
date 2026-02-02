package com.amadeus.domain.service;

import com.amadeus.domain.model.User;
import com.amadeus.domain.repository.UserRepository;
import com.amadeus.web.dto.CreateUserRequest;
import com.amadeus.web.dto.UpdateUserRequest;
import com.amadeus.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Servicio de dominio para User
 * 
 * SOLID Principles:
 * - Single Responsibility: Solo lógica de negocio de usuarios
 * - Dependency Inversion: Depende de interfaces (UserRepository,
 * PasswordEncoder)
 * 
 * @Service - Marca como componente de servicio Spring
 * @RequiredArgsConstructor - Lombok genera constructor con campos final (DI)
 * @Transactional - Gestión automática de transacciones BD
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crea un nuevo usuario
     * 
     * Responsabilidades:
     * 1. Validar que email no existe
     * 2. Validar que username no existe
     * 3. Hashear password (NUNCA guardar en texto plano)
     * 4. Persistir usuario
     * 
     * @param request Datos del nuevo usuario
     * @return Usuario creado
     * @throws IllegalArgumentException si email o username ya existe
     */
    public User createUser(CreateUserRequest request) {
        // Validación: email único
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "Email already exists: " + request.email());
        }

        // Validación: username único
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException(
                    "Username already exists: " + request.username());
        }

        // Hashear password con BCrypt
        String hashedPassword = passwordEncoder.encode(request.password());

        // Convertir Request -> Entity
        User user = UserMapper.toEntity(request, hashedPassword);

        // Persistir
        return userRepository.save(user);
    }

    /**
     * Busca usuario por ID
     * 
     * @param id ID del usuario
     * @return Usuario encontrado
     * @throws IllegalArgumentException si no existe
     */
    @Transactional(readOnly = true) // Optimización para solo lectura
    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found with id: " + id));
    }

    /**
     * Busca usuario por email
     * 
     * @param email Email del usuario
     * @return Optional con usuario si existe
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Actualiza perfil de usuario
     * 
     * Solo actualiza campos NO nulos en UpdateUserRequest
     * 
     * @param id      ID del usuario
     * @param request Datos a actualizar
     * @return Usuario actualizado
     * @throws IllegalArgumentException si usuario no existe
     */
    public User updateUser(String id, UpdateUserRequest request) {
        User user = findById(id); // Lanza excepción si no existe

        // Actualizar solo campos enviados
        UserMapper.updateEntity(user, request);

        return userRepository.save(user);
    }

    /**
     * Elimina un usuario
     * 
     * @param id ID del usuario a eliminar
     * @throws IllegalArgumentException si usuario no existe
     */
    public void deleteUser(String id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    /**
     * Verifica si un email ya está registrado
     * 
     * @param email Email a verificar
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Verifica si un username ya está registrado
     * 
     * @param username Username a verificar
     * @return true si existe, false si no
     */
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
