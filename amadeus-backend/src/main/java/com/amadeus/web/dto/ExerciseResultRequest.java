package com.amadeus.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ExerciseResultRequest(
        @NotBlank(message = "Exercise id is required") String exerciseId,
        @NotBlank(message = "Submitted answer is required") String submittedAnswer) {
}