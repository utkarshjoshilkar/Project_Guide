package com.studentguide.platform.github.guidance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuidanceAction {
    private String taskTitle;
    private String actionType;
    private String explanation;
}
