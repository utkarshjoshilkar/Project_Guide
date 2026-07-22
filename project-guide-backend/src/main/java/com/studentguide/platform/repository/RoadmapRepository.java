package com.studentguide.platform.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;

public interface RoadmapRepository extends JpaRepository<Roadmap,Long>{
    Optional<Roadmap> findByProject(Project project);

    Optional<Roadmap> findByProjectId(Long projectId);
}
