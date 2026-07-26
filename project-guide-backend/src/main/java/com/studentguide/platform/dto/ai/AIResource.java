package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Mirrors FastAPI Resource Pydantic model. */
@Getter
@Setter
@NoArgsConstructor
public class AIResource {

    @JsonProperty("resource_name")
    private String resourceName;

    @JsonProperty("url")
    private String url;

    /**
     * Type string from AI (e.g. "YouTube", "Article", "GitHub").
     * Mapped to ResourceType enum by RoadmapPersistenceService.
     */
    @JsonProperty("type")
    private String type;
}
