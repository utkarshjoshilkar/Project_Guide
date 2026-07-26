package com.studentguide.platform.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.MilestoneResponse;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.entity.Roadmap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MilestoneService — manages milestone retrieval and status transitions.
 *
 * Refactored (Phase 2):
 *   - getProfileByEmail() removed; replaced by {@link ProfileResolver}.
 *   - Inline ownership assertions removed; replaced by {@link OwnershipValidator}.
 *   - Removed repository dependencies: UserRepository, StudentProfileRepository.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final RoadmapRepository roadmapRepository;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;

    /**
     * Converts Milestone entity → DTO.
     */
    private MilestoneResponse toResponse(Milestone milestone) {
        return new MilestoneResponse(
                milestone.getId(),
                milestone.getRoadmap().getId(),
                milestone.getTitle(),
                milestone.getDescription(),
                milestone.getSequenceOrder(),
                milestone.getEstimatedDays(),
                milestone.getStatus());
    }

    /**
     * GET /api/roadmaps/{roadmapId}/milestones
     * Returns all milestones for a roadmap, ordered by sequence.
     * Ownership-checked: the roadmap must belong to the caller's project.
     */
    @Transactional(readOnly = true)
    public List<MilestoneResponse> getMilestones(String email, Long roadmapId) {
        StudentProfile profile = profileResolver.resolve(email);

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() -> new ResourceNotFoundException("Roadmap", "id", roadmapId));

        ownershipValidator.assertOwnsRoadmap(profile, roadmap);

        return milestoneRepository.findByRoadmapIdOrderBySequenceOrderAsc(roadmapId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * PATCH /api/roadmaps/{roadmapId}/milestones/{milestoneId}/status
     * Updates the status of a single milestone.
     * Ownership-checked: the milestone must belong to the caller's roadmap/project.
     */
    public MilestoneResponse updateMilestoneStatus(
            String email,
            Long milestoneId,
            MilestoneStatus newStatus) {

        StudentProfile profile = profileResolver.resolve(email);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone", "id", milestoneId));

        // Navigate: Milestone → Roadmap → Project (session is alive — @Transactional on this method)
        ownershipValidator.assertOwnsMilestone(profile, milestone);

        milestone.setStatus(newStatus);
        log.info("Milestone status updated: milestoneId={}, newStatus={}", milestoneId, newStatus);
        return toResponse(milestoneRepository.save(milestone));
    }
}