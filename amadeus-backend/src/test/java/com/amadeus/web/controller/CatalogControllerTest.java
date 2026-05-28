package com.amadeus.web.controller;

import com.amadeus.domain.service.CatalogService;
import com.amadeus.infrastructure.security.JwtAuthenticationFilter;
import com.amadeus.web.dto.CourseTreeDTO;
import com.amadeus.web.dto.LessonTreeDTO;
import com.amadeus.web.dto.LevelTreeDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CatalogController Integration Tests")
class CatalogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CatalogService catalogService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    @DisplayName("GET /api/catalog/tree - Should return course tree")
    void shouldReturnCourseTree() throws Exception {
        CourseTreeDTO tree = new CourseTreeDTO(List.of(
                new LevelTreeDTO(
                        "level-notes-1",
                        1,
                        "Primeres Notes",
                        "Descripcio",
                        "beginner",
                        List.of(new LessonTreeDTO("lesson-notes-1", 1, "Do, Re i ritme", "Demo", "notes", 5, 3)))));

        when(catalogService.getCourseTree()).thenReturn(tree);

        mockMvc.perform(get("/api/catalog/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.levels[0].id").value("level-notes-1"))
                .andExpect(jsonPath("$.levels[0].lessons[0].exerciseCount").value(3));
    }
}
