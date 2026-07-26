package com.studentguide.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.TaskRequest;
import com.studentguide.platform.dto.TaskResponse;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.entity.TaskStatus;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TaskService — manages the full CRUD lifecycle of tasks within milestones.
 *
 * Refactored (Phase 2):
 *   - getProfileByEmail() removed; replaced by {@link ProfileResolver}.
 *   - Private assertOwnership() helper removed; replaced by {@link OwnershipValidator}.
 *   - Removed repository dependencies: UserRepository, StudentProfileRepository.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;

    /**
     * Maps a Task entity to its DTO.
     */
    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getMilestone().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getCreatedAt(),
                task.getUpdatedAt());
    }

    /**
     * GET /api/milestones/{milestoneId}/tasks
     * Returns all tasks for a milestone ordered by creation time.
     * Ownership-checked: milestone must belong to the caller's project.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForMilestone(String email, Long milestoneId) {
        StudentProfile profile = profileResolver.resolve(email);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", milestoneId));

        ownershipValidator.assertOwnsMilestone(profile, milestone);

        return taskRepository.findByMilestoneIdOrderByCreatedAtAsc(milestoneId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * POST /api/milestones/{milestoneId}/tasks
     * Creates a new task under the given milestone with status TODO.
     * Ownership-checked.
     */
    public TaskResponse createTask(String email, Long milestoneId, TaskRequest request) {
        StudentProfile profile = profileResolver.resolve(email);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", milestoneId));

        ownershipValidator.assertOwnsMilestone(profile, milestone);

        Task task = Task.builder()
                .milestone(milestone)
                .title(request.getTitle())
                .description(request.getDescription())
                // status and timestamps set by @PrePersist
                .build();

        Task saved = taskRepository.save(task);
        log.info("Task created: taskId={}, milestoneId={}", saved.getId(), milestoneId);
        return toResponse(saved);
    }

    /**
     * PUT /api/tasks/{taskId}
     * Updates the title and description of an existing task.
     * Ownership-checked.
     */
    public TaskResponse updateTask(String email, Long taskId, TaskRequest request) {
        StudentProfile profile = profileResolver.resolve(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        ownershipValidator.assertOwnsTask(profile, task);

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        return toResponse(taskRepository.save(task));
    }

    /**
     * DELETE /api/tasks/{taskId}
     * Deletes a task permanently.
     * Ownership-checked.
     */
    public void deleteTask(String email, Long taskId) {
        StudentProfile profile = profileResolver.resolve(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        ownershipValidator.assertOwnsTask(profile, task);

        taskRepository.delete(task);
        log.info("Task deleted: taskId={}", taskId);
    }

    /**
     * PATCH /api/tasks/{taskId}/complete
     * Marks a task as DONE.
     * Ownership-checked.
     */
    public TaskResponse completeTask(String email, Long taskId) {
        StudentProfile profile = profileResolver.resolve(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        ownershipValidator.assertOwnsTask(profile, task);

        task.setStatus(TaskStatus.DONE);
        log.info("Task completed: taskId={}", taskId);
        return toResponse(taskRepository.save(task));
    }
}
