package com.studentguide.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.studentguide.platform.dto.ProjectRequest;
import com.studentguide.platform.dto.ProjectResponse;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.ProjectStatus;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────────
    // Helper: resolve the logged-in user's email → StudentProfile
    // Throws a clear exception at each step so the client gets
    // a meaningful error (404) rather than a NullPointerException.
    // ─────────────────────────────────────────────
    private StudentProfile getProfileByEmail(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();

        return studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", userId));
    }

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
        StudentProfile profile = getProfileByEmail(email);

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

        return toResponse(projectRepository.save(project));
    }

    /**
     * GET /api/projects/my
     * Returns all projects belonging to the logged-in student.
     */
    public List<ProjectResponse> getMyProjects(String email) {
        StudentProfile profile = getProfileByEmail(email);

        return projectRepository.findByStudentProfile(profile)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * GET /api/projects/{id}
     * Returns a single project by ID — only if it belongs to the logged-in student.
     * This prevents one student from reading another's project.
     */
    public ProjectResponse getProjectById(String email, Long projectId) {
        StudentProfile profile = getProfileByEmail(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // Ownership check — 403 if the project belongs to someone else.
        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Access denied: this project does not belong to you.");
        }

        return toResponse(project);
    }

    /**
     * PUT /api/projects/{id}
     * Full update of a project's details (all fields replaced).
     * Only the owner can update their project.
     */
    public ProjectResponse updateProject(String email, Long projectId, ProjectRequest request) {
        StudentProfile profile = getProfileByEmail(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Access denied: this project does not belong to you.");
        }

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
     * Changes only the status field — useful for lifecycle transitions
     * (e.g. IDEA_SUBMITTED → IN_PROGRESS → COMPLETED).
     */
    public ProjectResponse updateStatus(String email, Long projectId, ProjectStatus newStatus) {
        StudentProfile profile = getProfileByEmail(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Access denied: this project does not belong to you.");
        }

        project.setStatus(newStatus);
        return toResponse(projectRepository.save(project));
    }

    /**
     * DELETE /api/projects/{id}
     * Deletes a project — only if the logged-in student owns it.
     */
    public void deleteProject(String email, Long projectId) {
        StudentProfile profile = getProfileByEmail(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Access denied: this project does not belong to you.");
        }

        projectRepository.delete(project);
    }
}
