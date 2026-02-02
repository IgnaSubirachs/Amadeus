package com.amadeus.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test unitario para la entidad User
 * 
 * Filosofía TDD:
 * 1. RED: Escribir test que falla
 * 2. GREEN: Implementar código mínimo para pasar
 * 3. REFACTOR: Mejorar código manteniendo tests verdes
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        // Preparar un usuario de ejemplo para cada test
        user = User.builder()
                .id("user-123")
                .email("test@amadeus.com")
                .username("testuser")
                .passwordHash("$2a$10$hashedPassword")
                .createdAt(LocalDateTime.now())
                .lastLogin(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create a User with all required fields")
    void shouldCreateUserWithAllFields() {
        // ASSERT
        assertNotNull(user, "User should not be null");
        assertEquals("user-123", user.getId());
        assertEquals("test@amadeus.com", user.getEmail());
        assertEquals("testuser", user.getUsername());
        assertEquals("$2a$10$hashedPassword", user.getPasswordHash());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getLastLogin());
    }

    @Test
    @DisplayName("Should create User with builder pattern")
    void shouldUseBuilderPattern() {
        // ARRANGE & ACT
        User newUser = User.builder()
                .email("new@amadeus.com")
                .username("newuser")
                .passwordHash("hashedPass")
                .build();

        // ASSERT
        assertNotNull(newUser);
        assertEquals("new@amadeus.com", newUser.getEmail());
        assertEquals("newuser", newUser.getUsername());
    }

    @Test
    @DisplayName("Two users with same ID should be equal")
    void shouldBeEqualWhenSameId() {
        // ARRANGE
        User user1 = User.builder().id("same-id").email("user1@test.com").build();
        User user2 = User.builder().id("same-id").email("user2@test.com").build();

        // ACT & ASSERT
        assertEquals(user1, user2, "Users with same ID should be equal");
    }

    @Test
    @DisplayName("Two users with different IDs should not be equal")
    void shouldNotBeEqualWhenDifferentId() {
        // ARRANGE
        User user1 = User.builder().id("id-1").email("user@test.com").build();
        User user2 = User.builder().id("id-2").email("user@test.com").build();

        // ACT & ASSERT
        assertNotEquals(user1, user2, "Users with different IDs should not be equal");
    }

    @Test
    @DisplayName("HashCode should be consistent with equals")
    void shouldHaveConsistentHashCode() {
        // ARRANGE
        User user1 = User.builder().id("same-id").email("user1@test.com").build();
        User user2 = User.builder().id("same-id").email("user2@test.com").build();

        // ACT & ASSERT
        assertEquals(user1.hashCode(), user2.hashCode(),
                "Equal users should have same hashCode");
    }

    @Test
    @DisplayName("Should allow null profile image")
    void shouldAllowNullProfileImage() {
        // ARRANGE
        User userWithoutImage = User.builder()
                .email("test@test.com")
                .username("test")
                .passwordHash("hash")
                .build();

        // ASSERT
        assertNull(userWithoutImage.getProfileImage(),
                "Profile image should be nullable");
    }

    @Test
    @DisplayName("CreatedAt should default to current time if not set")
    void shouldSetCreatedAtAutomatically() {
        // ARRANGE
        LocalDateTime before = LocalDateTime.now().minusSeconds(1);

        // ACT
        User newUser = User.builder()
                .email("auto@test.com")
                .username("auto")
                .passwordHash("hash")
                .build();
        newUser.prePersist(); // Método del ciclo de vida JPA

        LocalDateTime after = LocalDateTime.now().plusSeconds(1);

        // ASSERT
        assertNotNull(newUser.getCreatedAt());
        assertTrue(newUser.getCreatedAt().isAfter(before) &&
                newUser.getCreatedAt().isBefore(after),
                "CreatedAt should be set to current time");
    }
}
