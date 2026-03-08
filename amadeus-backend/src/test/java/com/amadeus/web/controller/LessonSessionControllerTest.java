package com.amadeus.web.controller;

import com.amadeus.domain.model.ExerciseType;
import com.amadeus.domain.service.LessonSessionService;
import com.amadeus.infrastructure.security.JwtAuthenticationFilter;
import com.amadeus.web.dto.ExerciseResultRequest;
import com.amadeus.web.dto.ExerciseViewDTO;
import com.amadeus.web.dto.LessonSessionDTO;
import com.amadeus.web.dto.ProgressSummaryDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LessonSessionController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("LessonSessionController Integration Tests")
class LessonSessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LessonSessionService lessonSessionService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("POST /api/users/{userId}/lessons/{lessonId}/start - Should return lesson session")
    void shouldStartLesson() throws Exception {
        LessonSessionDTO session = new LessonSessionDTO(
                "lesson-1",
                "Notes a la clau de sol",
                "Identifica notes basiques",
                "notes",
                5,
                0,
                0,
                false,
                List.of(new ExerciseViewDTO("exercise-1", ExerciseType.NOTE_IDENTIFICATION, "easy", 1, "{\"note\":\"C4\"}", 10)));

        when(lessonSessionService.startLesson("user-1", "lesson-1")).thenReturn(session);

        mockMvc.perform(post("/api/users/user-1/lessons/lesson-1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lessonId").value("lesson-1"))
                .andExpect(jsonPath("$.exercises[0].id").value("exercise-1"))
                .andExpect(jsonPath("$.exercises[0].type").value("NOTE_IDENTIFICATION"));
    }

    @Test
    @DisplayName("POST /api/users/{userId}/lessons/{lessonId}/answer - Should update progress")
    void shouldSubmitAnswer() throws Exception {
        ExerciseResultRequest request = new ExerciseResultRequest("exercise-1", "Do");
        ProgressSummaryDTO summary = new ProgressSummaryDTO(
                "lesson-1",
                true,
                false,
                1,
                3,
                10,
                true,
                10,
                null);

        when(lessonSessionService.submitAnswer(eq("user-1"), eq("lesson-1"), any(ExerciseResultRequest.class)))
                .thenReturn(summary);

        mockMvc.perform(post("/api/users/user-1/lessons/lesson-1/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(10))
                .andExpect(jsonPath("$.answerCorrect").value(true))
                .andExpect(jsonPath("$.awardedPoints").value(10))
                .andExpect(jsonPath("$.currentExerciseIndex").value(1));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/lessons/progress - Should return progress list")
    void shouldGetProgress() throws Exception {
        ProgressSummaryDTO summary = new ProgressSummaryDTO(
                "lesson-1",
                true,
                true,
                3,
                3,
                30,
                null,
                0,
                LocalDateTime.of(2026, 3, 8, 10, 0));

        when(lessonSessionService.getUserProgress("user-1")).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/users/user-1/lessons/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].completed").value(true))
                .andExpect(jsonPath("$[0].totalExercises").value(3));
    }
}