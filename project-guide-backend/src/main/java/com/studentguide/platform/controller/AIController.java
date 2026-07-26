package com.studentguide.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.AIResponse;
import com.studentguide.platform.service.ai.AIIntegrationService;

import lombok.RequiredArgsConstructor;

/**
 * AIController — exposes the AI roadmap generation endpoint.
 *
 * Thin by design: no business logic, no persistence.
 * Delegates entirely to AIIntegrationService.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class AIController {

    private final AIIntegrationService aiIntegrationService;

    /**
     * POST /api/projects/{projectId}/generate-roadmap
     *
     * Triggers AI roadmap generation for the given project.
     * The project must belong to the authenticated user.
     *
     * Returns a confirmation with the new roadmapId on success.
     * Returns 409 CONFLICT if a roadmap already exists.
     * Returns 502 BAD_GATEWAY if the AI service is unavailable.
     */
    @PostMapping("/{projectId}/generate-roadmap")
    public ResponseEntity<AIResponse> generateRoadmap(
            Authentication authentication,
            @PathVariable Long projectId) {

        AIResponse response = aiIntegrationService.generateRoadmap(
                authentication.getName(), projectId);

        return ResponseEntity.ok(response);
    }
}
