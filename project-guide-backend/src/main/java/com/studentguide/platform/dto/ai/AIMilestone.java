package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mirrors FastAPI Milestone Pydantic model. */
@Getter
@Setter
@NoArgsConstructor
public class AIMilestone {

    @JsonProperty("milestone_id")
    private int milestoneId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("target_week")
    private String targetWeek;

    @JsonProperty("description")
    private String description;
}
