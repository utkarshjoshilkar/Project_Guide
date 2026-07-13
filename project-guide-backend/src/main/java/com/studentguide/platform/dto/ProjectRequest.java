package com.studentguide.platform.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 150, message = "Project title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Project description is required")
    @Size(max = 1000, message = "Project description cannot exceed 1000 characters")
    private String description;

    @NotBlank(message = "Domain is required")
    private String domain;

    @NotBlank(message = "Preferred tech stack is required")
    private String preferredTechStack;

    @NotBlank(message = "Skill level is required")
    private String skillLevel;

    @NotNull(message = "Deadline is required")
    private LocalDate deadline;

    @NotBlank(message = "Expected outcome is required")
    @Size(max = 1000, message = "Expected outcome cannot exceed 1000 characters")
    private String expectedOutcome;
}