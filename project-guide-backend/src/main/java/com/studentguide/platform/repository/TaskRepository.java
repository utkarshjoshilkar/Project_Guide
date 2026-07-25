package com.studentguide.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.studentguide.platform.entity.Task;
import com.studentguide.platform.entity.TaskStatus;

public interface TaskRepository extends JpaRepository<Task, Long> {

    // ── Per-milestone queries ───────────────────────────────────────────────

    List<Task> findByMilestoneIdOrderByCreatedAtAsc(Long milestoneId);

    long countByMilestoneId(Long milestoneId);

    long countByMilestoneIdAndStatus(Long milestoneId, TaskStatus status);

    // ── Cross-milestone queries (used by ProgressService for roadmap level) ─

    /**
     * Total tasks across all milestones in a roadmap.
     * Uses JPQL traversal: Task.milestone.roadmap.id
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.milestone.roadmap.id = :roadmapId")
    long countByMilestoneRoadmapId(@Param("roadmapId") Long roadmapId);

    /**
     * Tasks with a specific status across all milestones in a roadmap.
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.milestone.roadmap.id = :roadmapId AND t.status = :status")
    long countByMilestoneRoadmapIdAndStatus(
            @Param("roadmapId") Long roadmapId,
            @Param("status") TaskStatus status);

    // ── Cross-roadmap queries (used by ProgressService for project level) ───

    /**
     * Total tasks across the entire project's roadmap.
     * Traversal: Task.milestone.roadmap.project.id
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.milestone.roadmap.project.id = :projectId")
    long countByMilestoneRoadmapProjectId(@Param("projectId") Long projectId);

    /**
     * Tasks with a specific status for an entire project.
     */
    @Query("SELECT COUNT(t) FROM Task t WHERE t.milestone.roadmap.project.id = :projectId AND t.status = :status")
    long countByMilestoneRoadmapProjectIdAndStatus(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status);
}
