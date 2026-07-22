package com.studentguide.platform.controller;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.RoadmapResponse;
import com.studentguide.platform.service.RoadmapService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class RoadmapController {
    
    private final RoadmapService roadmapService;

    @GetMapping("/{projectId}/roadmap")
    public ResponseEntity<RoadmapResponse> getRoadmap(
            Authentication authentication,
            @PathVariable Long projectId) {

        RoadmapResponse response =
                roadmapService.getRoadmap(
                        authentication.getName(),
                        projectId);

        return ResponseEntity.ok(response);
    }

}
