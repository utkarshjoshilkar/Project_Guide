package com.studentguide.platform.github.guidance.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdaptiveGuidanceResponse {
    private Long projectId;
    private List<GuidanceAction> recommendedActions;
}
