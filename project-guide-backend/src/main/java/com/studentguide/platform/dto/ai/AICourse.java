package com.studentguide.platform.dto.ai;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AICourse {

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("course_name")
    private String courseName;

    @JsonProperty("price")
    private String price;

    @JsonProperty("link")
    private String link;
}
