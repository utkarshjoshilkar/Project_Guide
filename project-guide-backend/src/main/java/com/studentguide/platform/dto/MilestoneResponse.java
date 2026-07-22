package com.studentguide.platform.dto;

import com.studentguide.platform.entity.MilestoneStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MilestoneResponse {

    private Long id;

    private Long roadmapId;

    private String title;

    private String description;

    private Integer sequenceOrder;

    private Integer estimatedDays;

    private MilestoneStatus status;
}