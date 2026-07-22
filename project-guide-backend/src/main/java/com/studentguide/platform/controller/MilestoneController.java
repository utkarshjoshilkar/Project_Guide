package com.studentguide.platform.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.MilestoneResponse;
import com.studentguide.platform.service.MilestoneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmaps")
public class MilestoneController {

    private final MilestoneService milestoneService;

    @GetMapping("/{roadmapId}/milestones")
    public ResponseEntity<List<MilestoneResponse>> getMilestones(
            Authentication authentication,
            @PathVariable Long roadmapId) {

        List<MilestoneResponse> response =
                milestoneService.getMilestones(
                        authentication.getName(),
                        roadmapId);

        return ResponseEntity.ok(response);
    }
}