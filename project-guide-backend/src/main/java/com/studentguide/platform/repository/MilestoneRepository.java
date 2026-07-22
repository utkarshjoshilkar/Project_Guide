package com.studentguide.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Roadmap;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {

    List<Milestone> findByRoadmap(Roadmap roadmap);

    List<Milestone> findByRoadmapIdOrderBySequenceOrderAsc(Long roadmapId);

    // Used by RoadmapService and DashboardService to calculate progress percentage
    long countByRoadmapId(Long roadmapId);

    long countByRoadmapIdAndStatus(Long roadmapId, MilestoneStatus status);
}