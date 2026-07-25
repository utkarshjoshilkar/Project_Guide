package com.studentguide.platform.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.TaskRequest;
import com.studentguide.platform.dto.TaskResponse;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.entity.TaskStatus;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.TaskRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final MilestoneRepository milestoneRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    // ────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Resolves the authenticated user's StudentProfile from their email.
     */
    private StudentProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", user.getId()));
    }

    /**
     * Ownership guard: verifies that the milestone's project belongs to the
     * given student profile. Throws AccessDeniedException otherwise.
     */
    private void assertOwnership(Milestone milestone, StudentProfile profile) {
        Project project = milestone.getRoadmap().getProject();
        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException(
                    "Access denied: this milestone does not belong to you.");
        }
    }

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

    // ────────────────────────────────────────────────────────────────────────
    // Public API
    // ────────────────────────────────────────────────────────────────────────

    /**
     * GET /api/milestones/{milestoneId}/tasks
     * Returns all tasks for a milestone ordered by creation time.
     * Ownership-checked: milestone must belong to the caller's project.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksForMilestone(String email, Long milestoneId) {
        StudentProfile profile = getProfileByEmail(email);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", milestoneId));

        assertOwnership(milestone, profile);

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
        StudentProfile profile = getProfileByEmail(email);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", milestoneId));

        assertOwnership(milestone, profile);

        Task task = Task.builder()
                .milestone(milestone)
                .title(request.getTitle())
                .description(request.getDescription())
                // status and timestamps are set by @PrePersist
                .build();

        return toResponse(taskRepository.save(task));
    }

    /**
     * PUT /api/tasks/{taskId}
     * Updates the title and description of an existing task.
     * Ownership-checked.
     */
    public TaskResponse updateTask(String email, Long taskId, TaskRequest request) {
        StudentProfile profile = getProfileByEmail(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        assertOwnership(task.getMilestone(), profile);

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
        StudentProfile profile = getProfileByEmail(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        assertOwnership(task.getMilestone(), profile);

        taskRepository.delete(task);
    }

    /**
     * PATCH /api/tasks/{taskId}/complete
     * Marks a task as DONE.
     * Ownership-checked.
     */
    public TaskResponse completeTask(String email, Long taskId) {
        StudentProfile profile = getProfileByEmail(email);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        assertOwnership(task.getMilestone(), profile);

        task.setStatus(TaskStatus.DONE);

        return toResponse(taskRepository.save(task));
    }
}
