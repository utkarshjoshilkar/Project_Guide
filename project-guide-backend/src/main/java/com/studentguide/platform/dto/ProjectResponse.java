package com.studentguide.platform.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.studentguide.platform.entity.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String domain;
    private String preferredTechStack;
    private String skillLevel;
    private LocalDate deadline;
    private String expectedOutcome;
    private ProjectStatus status;

    // We expose the student profile's ID (not the full profile) to keep the response lean.
    private Long studentProfileId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
