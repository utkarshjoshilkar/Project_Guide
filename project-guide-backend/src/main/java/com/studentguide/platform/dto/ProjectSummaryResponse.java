package com.studentguide.platform.dto;

import com.studentguide.platform.entity.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A summary of one project's progress, returned inside DashboardResponse.
 *
 * Example:
 * {
 *   "projectId": 1,
 *   "projectName": "My ML Project",
 *   "status": "IN_PROGRESS",
 *   "roadmapProgress": 72.0,
 *   "totalMilestones": 6,
 *   "completedMilestones": 4,
 *   "totalTasks": 39,
 *   "completedTasks": 31,
 *   "pendingTasks": 8
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSummaryResponse {

    private Long projectId;

    private String projectName;

    /** Current lifecycle status of the project. */
    private ProjectStatus status;

    /**
     * Roadmap completion percentage (0.0–100.0), derived from task completion.
     * Falls back to milestone completion % if no tasks have been created yet.
     */
    private double roadmapProgress;

    /** Total number of milestones in the roadmap. */
    private int totalMilestones;

    /** Number of milestones with status COMPLETED. */
    private int completedMilestones;

    /** Total number of tasks across all milestones in the roadmap. */
    private long totalTasks;

    /** Number of tasks with status DONE. */
    private long completedTasks;

    /** Number of tasks that are not yet DONE (TODO + IN_PROGRESS). */
    private long pendingTasks;
}
