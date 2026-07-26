package com.studentguide.platform.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.ResourceRequest;
import com.studentguide.platform.dto.ResourceResponse;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Resource;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.ResourceRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.TaskRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final TaskRepository taskRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    // ────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────

    private StudentProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", user.getId()));
    }

    /**
     * Ownership guard: verifies that the task's milestone → roadmap → project
     * belongs to the given student profile. Throws AccessDeniedException otherwise.
     *
     * Traversal: Task → Milestone → Roadmap → Project → StudentProfile
     * Session kept alive by the outer @Transactional.
     */
    private void assertTaskOwnership(Task task, StudentProfile profile) {
        Milestone milestone = task.getMilestone();
        Project project = milestone.getRoadmap().getProject();
        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException(
                    "Access denied: this task does not belong to you.");
        }
    }

    /** Maps a Resource entity to its response DTO. */
    private ResourceResponse toResponse(Resource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getTask().getId(),
                resource.getTitle(),
                resource.getUrl(),
                resource.getType(),
                resource.getDescription(),
                resource.getCreatedAt());
    }

    // ────────────────────────────────────────────────────────────────────────
    // Public API
    // ────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/tasks/{taskId}/resources
     * Returns all resources for a task ordered by creation time.
     * Ownership-checked.
     */
    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesForTask(String email, Long taskId) {
        StudentProfile profile = getProfileByEmail(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        assertTaskOwnership(task, profile);

        return resourceRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * POST /api/tasks/{taskId}/resources
     * Adds a new resource to the given task.
     * Ownership-checked.
     */
    public ResourceResponse addResource(String email, Long taskId, ResourceRequest request) {
        StudentProfile profile = getProfileByEmail(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        assertTaskOwnership(task, profile);

        Resource resource = Resource.builder()
                .task(task)
                .title(request.getTitle())
                .url(request.getUrl())
                .type(request.getType())
                .description(request.getDescription())
                // createdAt is set by @PrePersist
                .build();

        return toResponse(resourceRepository.save(resource));
    }

    /**
     * PUT /api/resources/{resourceId}
     * Updates title, url, type, and description of an existing resource.
     * Ownership-checked.
     */
    public ResourceResponse updateResource(String email, Long resourceId, ResourceRequest request) {
        StudentProfile profile = getProfileByEmail(email);

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", resourceId));

        assertTaskOwnership(resource.getTask(), profile);

        resource.setTitle(request.getTitle());
        resource.setUrl(request.getUrl());
        resource.setType(request.getType());
        resource.setDescription(request.getDescription());

        return toResponse(resourceRepository.save(resource));
    }

    /**
     * DELETE /api/resources/{resourceId}
     * Permanently deletes a resource.
     * Ownership-checked.
     */
    public void deleteResource(String email, Long resourceId) {
        StudentProfile profile = getProfileByEmail(email);

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", resourceId));

        assertTaskOwnership(resource.getTask(), profile);

        resourceRepository.delete(resource);
    }
}
