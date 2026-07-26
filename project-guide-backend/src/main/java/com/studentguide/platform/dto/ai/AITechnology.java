package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AITechnology {

    @JsonProperty("name")
    private String name;

    @JsonProperty("purpose")
    private String purpose;
}
