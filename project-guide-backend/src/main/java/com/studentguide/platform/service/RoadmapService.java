package com.studentguide.platform.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.RoadmapResponse;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.RoadmapRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RoadmapService — retrieves and maps the roadmap for a given project.
 *
 * Refactored (Phase 2 + Phase 3):
 *   - getProfileByEmail() removed; replaced by {@link ProfileResolver}.
 *   - Inline ownership assertion removed; replaced by {@link OwnershipValidator}.
 *   - Removed repository dependencies: UserRepository, StudentProfileRepository,
 *     MilestoneRepository (no longer needed here).
 *
 * Phase 3 — Unified progress algorithm:
 *   Previously, toResponse() calculated progress by counting COMPLETED milestones
 *   (milestone-count algorithm). DashboardService used task-based progress via
 *   ProgressService. This produced DIFFERENT values for the same roadmap depending
 *   on which endpoint was called — a data consistency bug.
 *
 *   Fix: toResponse() now delegates to {@link ProgressService#calculateRoadmapProgress},
 *   establishing a SINGLE source of truth for progress across the entire application.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final ProjectRepository projectRepository;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;
    private final ProgressService progressService;

    /**
     * Converts a Roadmap entity to its DTO.
     *
     * Progress is always calculated via ProgressService (task-based), which is
     * the same algorithm used by DashboardService. This ensures a student sees
     * identical progress values regardless of which endpoint they call.
     */
    private RoadmapResponse toResponse(Roadmap roadmap) {
        double progress = progressService.calculateRoadmapProgress(roadmap.getId());

        return new RoadmapResponse(
                roadmap.getId(),
                roadmap.getProject().getId(),
                roadmap.getEstimatedDurationWeeks(),
                roadmap.getStatus(),
                roadmap.getGeneratedAt(),
                progress);
    }

    /**
     * GET /api/projects/{projectId}/roadmap
     * Returns the roadmap for a project.
     * Ownership-checked: the project must belong to the authenticated student.
     */
    public RoadmapResponse getRoadmap(String email, Long projectId) {
        StudentProfile profile = profileResolver.resolve(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);

        Roadmap roadmap = roadmapRepository.findByProject(project)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", "projectId", projectId));

        return toResponse(roadmap);
    }
}
