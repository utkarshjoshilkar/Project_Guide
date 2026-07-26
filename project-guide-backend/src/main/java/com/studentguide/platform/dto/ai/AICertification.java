package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AICertification {

    @JsonProperty("name")
    private String name;

    @JsonProperty("issuer")
    private String issuer;

    @JsonProperty("benefits")
    private String benefits;
}
