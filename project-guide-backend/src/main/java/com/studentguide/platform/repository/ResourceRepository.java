package com.studentguide.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.entity.Resource;
import com.studentguide.platform.entity.ResourceType;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    /** All resources for a task, creation-ordered. */
    List<Resource> findByTaskIdOrderByCreatedAtAsc(Long taskId);

    /** Resources filtered by type within a task. */
    List<Resource> findByTaskIdAndType(Long taskId, ResourceType type);
}
