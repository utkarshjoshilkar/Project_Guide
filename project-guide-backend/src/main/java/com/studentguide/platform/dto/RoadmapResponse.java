package com.studentguide.platform.dto;

import java.time.LocalDateTime;

import com.studentguide.platform.entity.RoadmapStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoadmapResponse {
    private Long id;

    private Long projectId;

    private Integer estimatedDurationWeeks;

    private RoadmapStatus status;

    private LocalDateTime generatedAt;

    private Double progressPercentage;
}
