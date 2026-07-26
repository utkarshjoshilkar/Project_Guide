package com.studentguide.platform.dto.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mirrors FastAPI RoadmapPhase Pydantic model.
 * action_items within each phase are mapped to Task entities
 * by RoadmapPersistenceService.
 */
@Getter
@Setter
@NoArgsConstructor
public class AIRoadmapPhase {

    @JsonProperty("phase")
    private String phase;

    @JsonProperty("timeline")
    private String timeline;

    @JsonProperty("weekly_allocation")
    private String weeklyAllocation;

    @JsonProperty("objectives")
    private List<String> objectives;

    @JsonProperty("topics_to_cover")
    private List<String> topicsToCover;

    /**
     * Action items are concrete, actionable steps within a phase.
     * Each item is persisted as a Task entity under the corresponding Milestone.
     */
    @JsonProperty("action_items")
    private List<String> actionItems;
}
