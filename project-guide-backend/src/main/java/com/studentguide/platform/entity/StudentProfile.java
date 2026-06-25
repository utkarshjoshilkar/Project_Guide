package com.studentguide.platform.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "student_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String college;
    private String branch;
    private int year;
    private double cgpa;
    private String skills;
    private String interests;
    private String preferredTechStack;
    private String githubProfile;
    private String linkedinProfile;
    private String learningGoal;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
