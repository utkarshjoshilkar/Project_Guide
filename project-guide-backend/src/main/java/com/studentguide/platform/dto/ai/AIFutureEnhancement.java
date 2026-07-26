package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AIFutureEnhancement {

    @JsonProperty("feature")
    private String feature;

    @JsonProperty("details")
    private String details;
}
