package com.studentguide.platform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.TaskRequest;
import com.studentguide.platform.dto.TaskResponse;
import com.studentguide.platform.service.TaskService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * GET /api/milestones/{milestoneId}/tasks
     * List all tasks under a milestone, ordered by creation time.
     */
    @GetMapping("/api/milestones/{milestoneId}/tasks")
    public ResponseEntity<List<TaskResponse>> getTasks(
            Authentication authentication,
            @PathVariable Long milestoneId) {

        List<TaskResponse> tasks =
                taskService.getTasksForMilestone(authentication.getName(), milestoneId);

        return ResponseEntity.ok(tasks);
    }

    /**
     * POST /api/milestones/{milestoneId}/tasks
     * Create a new task under the given milestone.
     * Responds with 201 Created.
     */
    @PostMapping("/api/milestones/{milestoneId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            Authentication authentication,
            @PathVariable Long milestoneId,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse created =
                taskService.createTask(authentication.getName(), milestoneId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/tasks/{taskId}
     * Update title and/or description of a task.
     */
    @PutMapping("/api/tasks/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            Authentication authentication,
            @PathVariable Long taskId,
            @Valid @RequestBody TaskRequest request) {

        TaskResponse updated =
                taskService.updateTask(authentication.getName(), taskId, request);

        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/tasks/{taskId}
     * Permanently delete a task.
     * Responds with 204 No Content.
     */
    @DeleteMapping("/api/tasks/{taskId}")
    public ResponseEntity<Void> deleteTask(
            Authentication authentication,
            @PathVariable Long taskId) {

        taskService.deleteTask(authentication.getName(), taskId);

        return ResponseEntity.noContent().build();
    }

    /**
     * PATCH /api/tasks/{taskId}/complete
     * Mark a task as DONE.
     */
    @PatchMapping("/api/tasks/{taskId}/complete")
    public ResponseEntity<TaskResponse> completeTask(
            Authentication authentication,
            @PathVariable Long taskId) {

        TaskResponse completed =
                taskService.completeTask(authentication.getName(), taskId);

        return ResponseEntity.ok(completed);
    }
}
