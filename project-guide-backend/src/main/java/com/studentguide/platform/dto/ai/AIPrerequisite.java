package com.studentguide.platform.dto.ai;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AIPrerequisite {

    @JsonProperty("topic")
    private String topic;

    @JsonProperty("concepts")
    private List<String> concepts;
}
