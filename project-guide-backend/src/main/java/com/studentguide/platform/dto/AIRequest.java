package com.studentguide.platform.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
 *
 * Bug fix (Phase 1): projectId was marked `transient` which prevents Java
 * serialization but does NOT stop Jackson JSON serialization. FastAPI's
 * RoadmapRequest Pydantic model has no `project_id` field, so sending it
 * causes a validation error. @JsonIgnore correctly excludes it from the
 * JSON body sent to FastAPI.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AIRequest {

    /**
     * Internal project ID — not sent to FastAPI.
     * Used for logging/traceability in AIIntegrationService only.
     */
    @JsonIgnore
    private Long projectId;

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
