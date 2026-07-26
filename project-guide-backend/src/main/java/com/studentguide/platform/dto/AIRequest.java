package com.studentguide.platform.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request body sent from Spring Boot to the FastAPI AI microservice.
 * Field names match the FastAPI RoadmapRequest Pydantic model (snake_case).
 *
 * Built by AIIntegrationService from the Project entity and
 * the student's profile data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRequest {

    /**
     * Internal project ID — not sent to FastAPI but carried for
     * traceability in logs. Excluded from serialization.
     */
    private transient Long projectId;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("project_description")
    private String projectDescription;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("year")
    private String year;

    @JsonProperty("experience_level")
    private String experienceLevel;

    @JsonProperty("current_skills")
    private List<String> currentSkills;

    @JsonProperty("timeline")
    private String timeline;

    @JsonProperty("weekly_hours")
    private int weeklyHours;

    @JsonProperty("learning_goal")
    private String learningGoal;
}
