package com.amadeus.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.amadeus.domain.model.User;
import com.amadeus.domain.service.UserService;
import com.amadeus.web.dto.CreateUserRequest;
import com.amadeus.web.dto.UpdateUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.amadeus.infrastructure.security.JwtService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para UserController
 * 
 * @WebMvcTest - Carga solo el contexto web (no toda la app)
 * @AutoConfigureMockMvc(addFilters = false) - Deshabilita filtros de seguridad
 *                                  MockMvc - Simula peticiones HTTP sin
 *                                  servidor real
 * @MockBean - Mock del servicio
 */
@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController Integration Tests")
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private UserService userService;

        @MockBean
        private JwtService jwtService;

        private User testUser;
        private CreateUserRequest createRequest;

        @BeforeEach
        void setUp() {
                testUser = User.builder()
                                .id("user-123")
                                .email("test@amadeus.com")
                                .username("testuser")
                                .passwordHash("$2a$10$hash")
                                .createdAt(LocalDateTime.now())
                                .build();

                createRequest = new CreateUserRequest(
                                "test@amadeus.com",
                                "testuser",
                                "password123");
        }

        @Test
        @DisplayName("POST /api/users - Should create user and return 201")
        void shouldCreateUser() throws Exception {
                // ARRANGE
                when(userService.createUser(any(CreateUserRequest.class)))
                                .thenReturn(testUser);

                // ACT & ASSERT
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value("user-123"))
                                .andExpect(jsonPath("$.email").value("test@amadeus.com"))
                                .andExpect(jsonPath("$.username").value("testuser"))
                                .andExpect(jsonPath("$.passwordHash").doesNotExist()); // NO exponer password
        }

        @Test
        @DisplayName("POST /api/users - Should return 400 for invalid email")
        void shouldReturn400ForInvalidEmail() throws Exception {
                // ARRANGE
                CreateUserRequest invalidRequest = new CreateUserRequest(
                                "invalid-email", // Email inválido
                                "testuser",
                                "password123");

                // ACT & ASSERT
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /api/users - Should return 400 for short password")
        void shouldReturn400ForShortPassword() throws Exception {
                // ARRANGE
                CreateUserRequest invalidRequest = new CreateUserRequest(
                                "test@test.com",
                                "testuser",
                                "short" // Password muy corto (< 8 caracteres)
                );

                // ACT & ASSERT
                mockMvc.perform(post("/api/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("GET /api/users/{id} - Should return user")
        void shouldGetUser() throws Exception {
                // ARRANGE
                when(userService.findById("user-123")).thenReturn(testUser);

                // ACT & ASSERT
                mockMvc.perform(get("/api/users/user-123"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value("user-123"))
                                .andExpect(jsonPath("$.email").value("test@amadeus.com"))
                                .andExpect(jsonPath("$.username").value("testuser"));
        }

        @Test
        @DisplayName("GET /api/users/{id} - Should return 400 for non-existent user")
        void shouldReturn400ForNonExistentUser() throws Exception {
                // ARRANGE
                when(userService.findById("nonexistent"))
                                .thenThrow(new IllegalArgumentException("User not found"));

                // ACT & ASSERT
                mockMvc.perform(get("/api/users/nonexistent"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PATCH /api/users/{id} - Should update user")
        void shouldUpdateUser() throws Exception {
                // ARRANGE
                UpdateUserRequest updateRequest = new UpdateUserRequest(
                                "newusername",
                                "https://new-avatar.jpg");

                User updatedUser = User.builder()
                                .id("user-123")
                                .email("test@amadeus.com")
                                .username("newusername")
                                .profileImage("https://new-avatar.jpg")
                                .passwordHash("$2a$10$hash")
                                .createdAt(LocalDateTime.now())
                                .build();

                when(userService.updateUser(eq("user-123"), any(UpdateUserRequest.class)))
                                .thenReturn(updatedUser);

                // ACT & ASSERT
                mockMvc.perform(patch("/api/users/user-123")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.username").value("newusername"))
                                .andExpect(jsonPath("$.profileImage").value("https://new-avatar.jpg"));
        }

        @Test
        @DisplayName("DELETE /api/users/{id} - Should delete user and return 204")
        void shouldDeleteUser() throws Exception {
                // ARRANGE
                doNothing().when(userService).deleteUser("user-123");

                // ACT & ASSERT
                mockMvc.perform(delete("/api/users/user-123"))
                                .andExpect(status().isNoContent());
        }
}
