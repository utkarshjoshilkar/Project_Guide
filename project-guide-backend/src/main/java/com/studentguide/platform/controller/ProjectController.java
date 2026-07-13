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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.ProjectRequest;
import com.studentguide.platform.dto.ProjectResponse;
import com.studentguide.platform.entity.ProjectStatus;
import com.studentguide.platform.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    /**
     * POST /api/projects
     * Submit a new project idea. Status defaults to IDEA_SUBMITTED.
     *
     * Body: ProjectRequest (title, description, domain, preferredTechStack,
     *       skillLevel, deadline, expectedOutcome)
     * Returns: 201 Created + ProjectResponse
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            Authentication authentication,
            @Valid @RequestBody ProjectRequest request) {

        ProjectResponse response = projectService.createProject(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/projects/my
     * Returns all projects belonging to the currently logged-in student.
     *
     * Returns: 200 OK + List<ProjectResponse>
     */
    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(Authentication authentication) {
        List<ProjectResponse> projects = projectService.getMyProjects(authentication.getName());
        return ResponseEntity.ok(projects);
    }

    /**
     * GET /api/projects/{id}
     * Returns a single project by ID — ownership-checked.
     *
     * Returns: 200 OK + ProjectResponse, or 404 if not found / 403 if not owner
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            Authentication authentication,
            @PathVariable Long id) {

        ProjectResponse response = projectService.getProjectById(authentication.getName(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/projects/{id}
     * Full update of all project fields — ownership-checked.
     *
     * Body: ProjectRequest
     * Returns: 200 OK + updated ProjectResponse
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {

        ProjectResponse response = projectService.updateProject(authentication.getName(), id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * PATCH /api/projects/{id}/status
     * Update only the status of a project (lifecycle transition).
     *
     * Query param: status=IN_PROGRESS  (one of IDEA_SUBMITTED, ROADMAP_GENERATED,
     *                                   IN_PROGRESS, ON_HOLD, COMPLETED)
     * Returns: 200 OK + updated ProjectResponse
     *
     * Example: PATCH /api/projects/3/status?status=IN_PROGRESS
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<ProjectResponse> updateStatus(
            Authentication authentication,
            @PathVariable Long id,
            @RequestParam ProjectStatus status) {

        ProjectResponse response = projectService.updateStatus(authentication.getName(), id, status);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/projects/{id}
     * Deletes a project — ownership-checked.
     *
     * Returns: 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
            Authentication authentication,
            @PathVariable Long id) {

        projectService.deleteProject(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
