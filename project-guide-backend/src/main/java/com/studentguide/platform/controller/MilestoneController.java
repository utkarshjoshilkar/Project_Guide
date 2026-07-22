package com.studentguide.platform.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.MilestoneResponse;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.service.MilestoneService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roadmaps")
public class MilestoneController {

    private final MilestoneService milestoneService;

    /**
     * GET /api/roadmaps/{roadmapId}/milestones
     * Returns all milestones for the given roadmap, ordered by sequence.
     */
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

    /**
     * PATCH /api/roadmaps/{roadmapId}/milestones/{milestoneId}/status?status=COMPLETED
     * Updates the status of a single milestone (Task module).
     *
     * Valid status values: NOT_STARTED, IN_PROGRESS, COMPLETED
     *
     * Example:
     *   PATCH /api/roadmaps/1/milestones/3/status?status=IN_PROGRESS
     */
    @PatchMapping("/{roadmapId}/milestones/{milestoneId}/status")
    public ResponseEntity<MilestoneResponse> updateMilestoneStatus(
            Authentication authentication,
            @PathVariable Long roadmapId,
            @PathVariable Long milestoneId,
            @RequestParam MilestoneStatus status) {

        MilestoneResponse response =
                milestoneService.updateMilestoneStatus(
                        authentication.getName(),
                        milestoneId,
                        status);

        return ResponseEntity.ok(response);
    }
}