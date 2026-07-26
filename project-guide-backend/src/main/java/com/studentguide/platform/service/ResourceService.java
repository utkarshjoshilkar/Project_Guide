package com.studentguide.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.ResourceRequest;
import com.studentguide.platform.dto.ResourceResponse;
import com.studentguide.platform.entity.Resource;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.ResourceRepository;
import com.studentguide.platform.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ResourceService — manages learning resources attached to tasks.
 *
 * Refactored (Phase 2):
 *   - getProfileByEmail() removed; replaced by {@link ProfileResolver}.
 *   - Private assertTaskOwnership() helper removed; replaced by {@link OwnershipValidator}.
 *   - Removed repository dependencies: UserRepository, StudentProfileRepository.
 *   - Removed unused entity imports: Milestone, Project, User.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final TaskRepository taskRepository;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;

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

    /**
     * GET /api/tasks/{taskId}/resources
     * Returns all resources for a task ordered by creation time.
     * Ownership-checked.
     */
    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesForTask(String email, Long taskId) {
        StudentProfile profile = profileResolver.resolve(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        ownershipValidator.assertOwnsTask(profile, task);

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
        StudentProfile profile = profileResolver.resolve(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        ownershipValidator.assertOwnsTask(profile, task);

        Resource resource = Resource.builder()
                .task(task)
                .title(request.getTitle())
                .url(request.getUrl())
                .type(request.getType())
                .description(request.getDescription())
                // createdAt is set by @PrePersist
                .build();

        Resource saved = resourceRepository.save(resource);
        log.info("Resource added: resourceId={}, taskId={}", saved.getId(), taskId);
        return toResponse(saved);
    }

    /**
     * PUT /api/resources/{resourceId}
     * Updates title, url, type, and description of an existing resource.
     * Ownership-checked.
     */
    public ResourceResponse updateResource(String email, Long resourceId, ResourceRequest request) {
        StudentProfile profile = profileResolver.resolve(email);

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", resourceId));

        ownershipValidator.assertOwnsResource(profile, resource);

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
        StudentProfile profile = profileResolver.resolve(email);

        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new ResourceNotFoundException("Resource", "id", resourceId));

        ownershipValidator.assertOwnsResource(profile, resource);

        resourceRepository.delete(resource);
        log.info("Resource deleted: resourceId={}", resourceId);
    }
}
