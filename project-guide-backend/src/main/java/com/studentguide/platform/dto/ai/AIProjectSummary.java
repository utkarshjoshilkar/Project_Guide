package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AIProjectSummary {

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("duration")
    private String duration;

    @JsonProperty("weekly_effort")
    private String weeklyEffort;
}
