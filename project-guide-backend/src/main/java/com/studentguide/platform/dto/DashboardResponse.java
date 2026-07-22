package com.studentguide.platform.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {

    /** The authenticated user's ID. */
    private Long userId;

    /** The authenticated user's full name. */
    private String fullName;

    /** The authenticated user's email. */
    private String email;

    /**
     * The student's profile, or null if they haven't created one yet.
     */
    private StudentProfileResponse profile;

    /** Total number of projects the student has submitted. */
    private int totalProjects;

    /**
     * Breakdown of project counts by status.
     * Keys are ProjectStatus names (e.g. "IDEA_SUBMITTED", "IN_PROGRESS").
     */
    private Map<String, Long> projectsByStatus;

    /**
     * Average completion percentage across all projects that have a roadmap.
     * Rounded to one decimal place. 0.0 if no roadmaps exist yet.
     */
    private double overallProgressPercentage;

    /**
     * Up to 3 most recently updated projects, useful for the dashboard
     * "Recent Activity" section.
     */
    private List<ProjectResponse> recentProjects;
}
