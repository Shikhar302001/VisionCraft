package com.shikhar.VisionCraft.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImageGenerationRequest(

        @NotBlank(message = "Prompt cannot be empty")
        @Size(max = 1000, message = "Prompt cannot exceed 1000 characters")
        String prompt

) { }