package com.amadeus.web.controller;

import com.amadeus.domain.model.User;
import com.amadeus.domain.service.UserService;
import com.amadeus.web.dto.CreateUserRequest;
import com.amadeus.web.dto.UpdateUserRequest;
import com.amadeus.web.dto.UserDTO;
import com.amadeus.web.mapper.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller para User
 * 
 * Endpoints:
 * POST /api/users - Crear usuario (registro)
 * GET /api/users/{id} - Obtener perfil de usuario
 * PATCH /api/users/{id} - Actualizar perfil
 * DELETE /api/users/{id} - Eliminar cuenta
 * 
 * @RestController - Combina @Controller + @ResponseBody
 * @RequestMapping - Prefijo de ruta para todos los endpoints
 * @RequiredArgsConstructor - Lombok genera constructor para final fields (DI)
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Crear nuevo usuario (Registro)
     * 
     * POST /api/users
     * Body: {"email": "...", "username": "...", "password": "..."}
     * 
     * @param request Datos del nuevo usuario
     * @return UserDTO (sin password) + status 201 Created
     */
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.createUser(request);
        UserDTO dto = UserMapper.toDTO(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Obtener perfil de usuario
     * 
     * GET /api/users/{id}
     * 
     * @param id ID del usuario
     * @return UserDTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String id) {
        User user = userService.findById(id);
        UserDTO dto = UserMapper.toDTO(user);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualizar perfil de usuario
     * 
     * PATCH /api/users/{id}
     * Body: {"username": "...", "profileImage": "..."}
     * 
     * Solo actualiza campos enviados (no nulls)
     * 
     * @param id      ID del usuario
     * @param request Datos a actualizar
     * @return UserDTO actualizado
     */
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {

        User user = userService.updateUser(id, request);
        UserDTO dto = UserMapper.toDTO(user);
        return ResponseEntity.ok(dto);
    }

    /**
     * Eliminar cuenta de usuario
     * 
     * DELETE /api/users/{id}
     * 
     * @param id ID del usuario
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
