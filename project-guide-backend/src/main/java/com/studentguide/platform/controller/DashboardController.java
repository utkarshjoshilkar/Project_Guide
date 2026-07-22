package com.studentguide.platform.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.DashboardResponse;
import com.studentguide.platform.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard
     *
     * Returns the authenticated student's full dashboard summary:
     *   - User info & profile
     *   - Project counts broken down by status
     *   - Overall progress percentage (avg across roadmapped projects)
     *   - Last 3 recently updated projects
     *
     * Requires: valid JWT token (handled by JwtAuthFilter).
     *
     * Example response:
     * {
     *   "userId": 1,
     *   "fullName": "Utkarsh",
     *   "email": "utkarsh@example.com",
     *   "profile": { ... },
     *   "totalProjects": 4,
     *   "projectsByStatus": { "IN_PROGRESS": 2, "IDEA_SUBMITTED": 2 },
     *   "overallProgressPercentage": 62.5,
     *   "recentProjects": [ ... ]
     * }
     */
    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {
        DashboardResponse response = dashboardService.getDashboard(authentication.getName());
        return ResponseEntity.ok(response);
    }
}
