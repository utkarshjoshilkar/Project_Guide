package com.studentguide.platform.dto.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Top-level DTO mirroring the FastAPI RoadmapResponse Pydantic model.
 * Field names use @JsonProperty to handle snake_case deserialization
 * from the FastAPI JSON response.
 */
@Getter
@Setter
@NoArgsConstructor
public class AIRoadmapResponse {

    @JsonProperty("project_summary")
    private AIProjectSummary projectSummary;

    @JsonProperty("prerequisites")
    private List<AIPrerequisite> prerequisites;

    @JsonProperty("phase_wise_learning_plan")
    private List<AIRoadmapPhase> phaseWiseLearningPlan;

    @JsonProperty("technologies_to_learn")
    private List<AITechnology> technologiesToLearn;

    @JsonProperty("learning_resources")
    private List<AIResource> learningResources;

    @JsonProperty("mini_projects")
    private List<AIMiniProject> miniProjects;

    @JsonProperty("milestones")
    private List<AIMilestone> milestones;

    @JsonProperty("recommended_courses")
    private List<AICourse> recommendedCourses;

    @JsonProperty("recommended_certifications")
    private List<AICertification> recommendedCertifications;

    @JsonProperty("future_enhancements")
    private List<AIFutureEnhancement> futureEnhancements;

    @JsonProperty("final_expected_outcome")
    private String finalExpectedOutcome;
}
