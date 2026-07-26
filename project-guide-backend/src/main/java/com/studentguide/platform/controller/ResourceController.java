package com.studentguide.platform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.ResourceRequest;
import com.studentguide.platform.dto.ResourceResponse;
import com.studentguide.platform.service.ResourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    /**
     * GET /api/tasks/{taskId}/resources
     * Returns all resources under a task, ordered by creation time.
     */
    @GetMapping("/api/tasks/{taskId}/resources")
    public ResponseEntity<List<ResourceResponse>> getResources(
            Authentication authentication,
            @PathVariable Long taskId) {

        return ResponseEntity.ok(
                resourceService.getResourcesForTask(authentication.getName(), taskId));
    }

    /**
     * POST /api/tasks/{taskId}/resources
     * Adds a learning resource to a task.
     * Responds with 201 Created.
     */
    @PostMapping("/api/tasks/{taskId}/resources")
    public ResponseEntity<ResourceResponse> addResource(
            Authentication authentication,
            @PathVariable Long taskId,
            @Valid @RequestBody ResourceRequest request) {

        ResourceResponse created =
                resourceService.addResource(authentication.getName(), taskId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/resources/{resourceId}
     * Updates an existing resource's details.
     */
    @PutMapping("/api/resources/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(
            Authentication authentication,
            @PathVariable Long resourceId,
            @Valid @RequestBody ResourceRequest request) {

        return ResponseEntity.ok(
                resourceService.updateResource(authentication.getName(), resourceId, request));
    }

    /**
     * DELETE /api/resources/{resourceId}
     * Permanently removes a resource.
     * Responds with 204 No Content.
     */
    @DeleteMapping("/api/resources/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            Authentication authentication,
            @PathVariable Long resourceId) {

        resourceService.deleteResource(authentication.getName(), resourceId);
        return ResponseEntity.noContent().build();
    }
}
