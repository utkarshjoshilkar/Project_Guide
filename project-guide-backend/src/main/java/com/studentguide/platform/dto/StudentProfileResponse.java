package com.studentguide.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    private Long id;

    private String college;

    private String branch;

    private Integer year;

    private Double cgpa;

    private String skills;

    private String interests;

    private String preferredTechStack;

    private String githubProfile;

    private String linkedinProfile;

    private String learningGoal;
}