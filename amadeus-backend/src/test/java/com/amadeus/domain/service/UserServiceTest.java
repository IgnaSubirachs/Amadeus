package com.amadeus.domain.service;

import com.amadeus.domain.model.User;
import com.amadeus.domain.repository.UserRepository;
import com.amadeus.web.dto.CreateUserRequest;
import com.amadeus.web.dto.UpdateUserRequest;
import com.amadeus.web.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests TDD para UserService
 * 
 * @ExtendWith(MockitoExtension.class) - Permite usar Mockito
 * 
 * @Mock - Crea mocks de dependencias
 * @InjectMocks - Crea instancia de UserService inyectando los mocks
 * 
 *              ¿Por qué mockear?
 *              - Tests unitarios (no necesitan BD real)
 *              - Rápidos y aislados
 *              - Controlamos el comportamiento de dependencias
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private CreateUserRequest createRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        createRequest = new CreateUserRequest(
                "test@amadeus.com",
                "testuser",
                "password123");

        savedUser = User.builder()
                .id("user-123")
                .email("test@amadeus.com")
                .username("testuser")
                .passwordHash("$2a$10$hashedPassword")
                .build();
    }

    @Test
    @DisplayName("Should create user with hashed password")
    void shouldCreateUserWithHashedPassword() {
        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // ACT
        User result = userService.createUser(createRequest);

        // ASSERT
        assertNotNull(result);
        assertEquals("test@amadeus.com", result.getEmail());
        verify(passwordEncoder).encode("password123"); // Verificar que hasheó
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void shouldThrowExceptionWhenEmailExists() {
        // ARRANGE
        when(userRepository.existsByEmail("test@amadeus.com")).thenReturn(true);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(createRequest);
        }, "Should throw exception for duplicate email");

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // ARRANGE
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(createRequest);
        }, "Should throw exception for duplicate username");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should find user by ID")
    void shouldFindUserById() {
        // ARRANGE
        when(userRepository.findById("user-123")).thenReturn(Optional.of(savedUser));

        // ACT
        User result = userService.findById("user-123");

        // ASSERT
        assertNotNull(result);
        assertEquals("user-123", result.getId());
        verify(userRepository).findById("user-123");
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFound() {
        // ARRANGE
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class, () -> {
            userService.findById("nonexistent");
        }, "Should throw exception when user not found");
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {
        // ARRANGE
        when(userRepository.findByEmail("test@amadeus.com"))
                .thenReturn(Optional.of(savedUser));

        // ACT
        Optional<User> result = userService.findByEmail("test@amadeus.com");

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("test@amadeus.com", result.get().getEmail());
    }

    @Test
    @DisplayName("Should update user profile")
    void shouldUpdateUserProfile() {
        // ARRANGE
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "newusername",
                "https://new-avatar.com/image.jpg");

        when(userRepository.findById("user-123")).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // ACT
        User result = userService.updateUser("user-123", updateRequest);

        // ASSERT
        assertNotNull(result);
        verify(userRepository).save(savedUser);
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // ARRANGE
        when(userRepository.findById("user-123")).thenReturn(Optional.of(savedUser));
        doNothing().when(userRepository).delete(any(User.class));

        // ACT
        userService.deleteUser("user-123");

        // ASSERT
        verify(userRepository).findById("user-123");
        verify(userRepository).delete(savedUser);
    }
}
