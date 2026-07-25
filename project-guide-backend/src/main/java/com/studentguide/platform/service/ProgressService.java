package com.studentguide.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.TaskStatus;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

/**
 * ProgressService — single source of truth for all calculated progress.
 *
 * Progress is NEVER stored in the database; it is always derived from the
 * current task/milestone state at query time.
 *
 * Hierarchy:
 *   Tasks (DONE count)
 *     → Milestone progress  (% of tasks done)
 *     → Roadmap progress    (average milestone progress)
 *     → Project progress    (== roadmap progress, 1:1 relationship)
 *
 * Fallback strategy:
 *   If a milestone has no tasks yet, it is treated as 0% complete
 *   (not yet started). The manual MilestoneStatus field is independent
 *   and is used separately for milestone-level status display.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProgressService {

    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final RoadmapRepository roadmapRepository;

    // ────────────────────────────────────────────────────────────────────────
    // Public API
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Calculates progress for a single milestone as the percentage of DONE tasks.
     * Returns 0.0 if the milestone has no tasks.
     *
     * @param milestoneId the milestone to evaluate
     * @return 0.0–100.0, rounded to one decimal place
     */
    public double calculateMilestoneProgress(Long milestoneId) {
        long total = taskRepository.countByMilestoneId(milestoneId);
        if (total == 0) return 0.0;
        long done = taskRepository.countByMilestoneIdAndStatus(milestoneId, TaskStatus.DONE);
        return round1dp(done * 100.0 / total);
    }

    /**
     * Calculates progress for a roadmap as the average of all its milestone
     * progress values.
     *
     * Falls back to counting COMPLETED milestones (via MilestoneStatus) if
     * the roadmap has milestones but none of them have tasks yet — this keeps
     * the existing manual status flow working during early development.
     *
     * Returns 0.0 if the roadmap has no milestones.
     *
     * @param roadmapId the roadmap to evaluate
     * @return 0.0–100.0, rounded to one decimal place
     */
    public double calculateRoadmapProgress(Long roadmapId) {
        List<Long> milestoneIds =
                milestoneRepository.findByRoadmapIdOrderBySequenceOrderAsc(roadmapId)
                        .stream()
                        .map(m -> m.getId())
                        .toList();

        if (milestoneIds.isEmpty()) return 0.0;

        // Check whether any tasks exist in this roadmap yet
        long totalTasks = taskRepository.countByMilestoneRoadmapId(roadmapId);

        if (totalTasks == 0) {
            // Fallback: use completed milestone count (manual status)
            long completedMilestones =
                    milestoneRepository.countByRoadmapIdAndStatus(roadmapId, MilestoneStatus.COMPLETED);
            return round1dp(completedMilestones * 100.0 / milestoneIds.size());
        }

        // Task-based: average each milestone's task completion %
        double average = milestoneIds.stream()
                .mapToDouble(this::calculateMilestoneProgress)
                .average()
                .orElse(0.0);

        return round1dp(average);
    }

    /**
     * Calculates progress for a project. Since each project has exactly one
     * roadmap, this delegates to calculateRoadmapProgress.
     *
     * Returns 0.0 if the project has no roadmap yet.
     *
     * @param projectId the project to evaluate
     * @return 0.0–100.0, rounded to one decimal place
     */
    public double calculateProjectProgress(Long projectId) {
        return roadmapRepository.findByProjectId(projectId)
                .map(roadmap -> calculateRoadmapProgress(roadmap.getId()))
                .orElse(0.0);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Utility
    // ────────────────────────────────────────────────────────────────────────

    private double round1dp(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
