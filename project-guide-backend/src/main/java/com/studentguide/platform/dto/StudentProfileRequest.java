package com.studentguide.platform.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileRequest {

    @NotBlank(message = "College is required")
    @Size(max = 150, message = "College name cannot exceed 150 characters")
    private String college;

    @NotBlank(message = "Branch is required")
    @Size(max = 100, message = "Branch cannot exceed 100 characters")
    private String branch;

    @Min(value = 1, message = "Year must be at least 1")
    @Max(value = 6, message = "Year cannot exceed 6")
    private Integer year;

    @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10")
    private Double cgpa;

    @Size(max = 500, message = "Skills cannot exceed 500 characters")
    private String skills;

    @Size(max = 500, message = "Interests cannot exceed 500 characters")
    private String interests;

    @Size(max = 150, message = "Preferred tech stack cannot exceed 150 characters")
    private String preferredTechStack;

    @Size(max = 255, message = "GitHub profile URL is too long")
    private String githubProfile;

    @Size(max = 255, message = "LinkedIn profile URL is too long")
    private String linkedinProfile;

    @Size(max = 500, message = "Learning goal cannot exceed 500 characters")
    private String learningGoal;

}