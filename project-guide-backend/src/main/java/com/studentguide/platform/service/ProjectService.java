package com.studentguide.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.ProjectRequest;
import com.studentguide.platform.dto.ProjectResponse;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.ProjectStatus;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProjectService — manages the full lifecycle of a student's project.
 *
 * Refactored (Phase 2):
 *   - getProfileByEmail() removed; replaced by injected {@link ProfileResolver}.
 *   - Inline ownership assertions removed; replaced by injected {@link OwnershipValidator}.
 *   - Removed unused repository dependencies: UserRepository, StudentProfileRepository.
 *   - Added @Slf4j for structured logging on mutations.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;

    // ─────────────────────────────────────────────
    // Helper: Project entity → ProjectResponse DTO
    // ─────────────────────────────────────────────
    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getDomain(),
                project.getPreferredTechStack(),
                project.getSkillLevel(),
                project.getDeadline(),
                project.getExpectedOutcome(),
                project.getStatus(),
                project.getStudentProfile().getId(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }

    /**
     * POST /api/projects
     * Creates a new project idea under the logged-in student's profile.
     * Status defaults to IDEA_SUBMITTED (set via @PrePersist on the entity).
     */
    public ProjectResponse createProject(String email, ProjectRequest request) {
        StudentProfile profile = profileResolver.resolve(email);

        Project project = Project.builder()
                .studentProfile(profile)
                .title(request.getTitle())
                .description(request.getDescription())
                .domain(request.getDomain())
                .preferredTechStack(request.getPreferredTechStack())
                .skillLevel(request.getSkillLevel())
                .deadline(request.getDeadline())
                .expectedOutcome(request.getExpectedOutcome())
                .build();

        Project saved = projectRepository.save(project);
        log.info("Project created: projectId={}, profileId={}", saved.getId(), profile.getId());
        return toResponse(saved);
    }

    /**
     * GET /api/projects/my
     * Returns all projects belonging to the logged-in student.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getMyProjects(String email) {
        StudentProfile profile = profileResolver.resolve(email);

        return projectRepository.findByStudentProfile(profile)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * GET /api/projects/{id}
     * Returns a single project by ID — only if it belongs to the logged-in student.
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(String email, Long projectId) {
        StudentProfile profile = profileResolver.resolve(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);
        return toResponse(project);
    }

    /**
     * PUT /api/projects/{id}
     * Full update of a project's details. Only the owner can update.
     */
    public ProjectResponse updateProject(String email, Long projectId, ProjectRequest request) {
        StudentProfile profile = profileResolver.resolve(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setDomain(request.getDomain());
        project.setPreferredTechStack(request.getPreferredTechStack());
        project.setSkillLevel(request.getSkillLevel());
        project.setDeadline(request.getDeadline());
        project.setExpectedOutcome(request.getExpectedOutcome());

        return toResponse(projectRepository.save(project));
    }

    /**
     * PATCH /api/projects/{id}/status
     * Changes only the status field — useful for lifecycle transitions.
     */
    public ProjectResponse updateStatus(String email, Long projectId, ProjectStatus newStatus) {
        StudentProfile profile = profileResolver.resolve(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);

        project.setStatus(newStatus);
        return toResponse(projectRepository.save(project));
    }

    /**
     * DELETE /api/projects/{id}
     * Deletes a project — only if the logged-in student owns it.
     */
    public void deleteProject(String email, Long projectId) {
        StudentProfile profile = profileResolver.resolve(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);

        projectRepository.delete(project);
        log.info("Project deleted: projectId={}, profileId={}", projectId, profile.getId());
    }
}
