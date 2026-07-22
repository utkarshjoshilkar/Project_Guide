package com.studentguide.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.Roadmap;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    
    List<Milestone> findByRoadmap(Roadmap roadmap);


    List<Milestone> findByRoadmapIdOrderBySequenceOrderAsc(Long roadmapId);
}