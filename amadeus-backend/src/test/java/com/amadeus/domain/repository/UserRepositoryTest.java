package com.amadeus.domain.repository;

import com.amadeus.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test de integración para UserRepository
 * 
 * @DataJpaTest - Configura un contexto de JPA para testing:
 *              - Usa base de datos en memoria (H2)
 *              - Configura auto Spring Data repositories
 *              - Hace rollback después de cada test (aislamiento)
 *              - No carga todo el contexto Spring (más rápido)
 * 
 *              @ActiveProfiles("test") - Usa application-test.yml
 * 
 *              TestEntityManager - Alternativa a JPA EntityManager para tests
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("UserRepository Integration Tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Preparar usuario de prueba
        testUser = User.builder()
                .email("test@amadeus.com")
                .username("testuser")
                .passwordHash("$2a$10$hashedPassword")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should save and retrieve user by ID")
    void shouldSaveAndRetrieveById() {
        // ARRANGE & ACT
        User savedUser = userRepository.save(testUser);
        entityManager.flush(); // Forzar ejecución de SQL
        entityManager.clear(); // Limpiar cache para forzar lectura desde BD

        // ASSERT
        assertNotNull(savedUser.getId(), "ID should be generated");

        Optional<User> found = userRepository.findById(savedUser.getId());
        assertTrue(found.isPresent(), "User should be found");
        assertEquals(testUser.getEmail(), found.get().getEmail());
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindByEmail() {
        // ARRANGE
        userRepository.save(testUser);
        entityManager.flush();

        // ACT
        Optional<User> found = userRepository.findByEmail("test@amadeus.com");

        // ASSERT
        assertTrue(found.isPresent(), "User should be found by email");
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void shouldReturnEmptyWhenEmailNotFound() {
        // ACT
        Optional<User> found = userRepository.findByEmail("nonexistent@test.com");

        // ASSERT
        assertFalse(found.isPresent(), "Should return empty Optional");
    }

    @Test
    @DisplayName("Should check if email exists")
    void shouldCheckIfEmailExists() {
        // ARRANGE
        userRepository.save(testUser);
        entityManager.flush();

        // ACT & ASSERT
        assertTrue(userRepository.existsByEmail("test@amadeus.com"),
                "Should return true for existing email");
        assertFalse(userRepository.existsByEmail("nonexistent@test.com"),
                "Should return false for non-existing email");
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindByUsername() {
        // ARRANGE
        userRepository.save(testUser);
        entityManager.flush();

        // ACT
        Optional<User> found = userRepository.findByUsername("testuser");

        // ASSERT
        assertTrue(found.isPresent(), "User should be found by username");
        assertEquals("test@amadeus.com", found.get().getEmail());
    }

    @Test
    @DisplayName("Should check if username exists")
    void shouldCheckIfUsernameExists() {
        // ARRANGE
        userRepository.save(testUser);
        entityManager.flush();

        // ACT & ASSERT
        assertTrue(userRepository.existsByUsername("testuser"),
                "Should return true for existing username");
        assertFalse(userRepository.existsByUsername("nonexistent"),
                "Should return false for non-existing username");
    }

    @Test
    @DisplayName("Should enforce unique email constraint")
    void shouldEnforceUniqueEmail() {
        // ARRANGE
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        User duplicateEmail = User.builder()
                .email("test@amadeus.com") // Mismo email
                .username("different")
                .passwordHash("hash")
                .build();

        // ACT & ASSERT
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateEmail);
            entityManager.flush(); // Aquí falla por constraint
        }, "Should throw exception for duplicate email");
    }

    @Test
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // ARRANGE
        User saved = userRepository.save(testUser);
        entityManager.flush();
        String userId = saved.getId();

        // ACT
        userRepository.delete(saved);
        entityManager.flush();

        // ASSERT
        Optional<User> found = userRepository.findById(userId);
        assertFalse(found.isPresent(), "User should be deleted");
    }

    @Test
    @DisplayName("Should count users")
    void shouldCountUsers() {
        // ARRANGE
        assertEquals(0, userRepository.count(), "Should start with 0 users");

        userRepository.save(testUser);
        userRepository.save(User.builder()
                .email("another@test.com")
                .username("another")
                .passwordHash("hash")
                .build());
        entityManager.flush();

        // ACT & ASSERT
        assertEquals(2, userRepository.count(), "Should have 2 users");
    }

    @Test
    @DisplayName("Should automatically set createdAt on persist")
    void shouldSetCreatedAtAutomatically() {
        // ARRANGE
        User userWithoutCreatedAt = User.builder()
                .email("auto@test.com")
                .username("auto")
                .passwordHash("hash")
                .build();

        // ACT
        User saved = userRepository.save(userWithoutCreatedAt);
        entityManager.flush();

        // ASSERT
        assertNotNull(saved.getCreatedAt(), "CreatedAt should be set automatically");
    }
}
