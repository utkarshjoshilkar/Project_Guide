package com.studentguide.platform.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.MilestoneResponse;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MilestoneService {

    private final MilestoneRepository milestoneRepository;
    private final RoadmapRepository roadmapRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    /**
     * Returns the logged-in student's profile using email.
     */
    private StudentProfile getProfileByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User",
                                "email",
                                email));

        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "StudentProfile",
                                "userId",
                                user.getId()));
    }

    /**
     * Converts Entity -> DTO
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

        StudentProfile profile = getProfileByEmail(email);

        Roadmap roadmap = roadmapRepository.findById(roadmapId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Roadmap",
                                "id",
                                roadmapId));

        Project project = roadmap.getProject();

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException(
                    "Access denied: this roadmap does not belong to you.");
        }

        List<Milestone> milestones =
                milestoneRepository.findByRoadmapIdOrderBySequenceOrderAsc(roadmapId);

        return milestones.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * PATCH /api/roadmaps/{roadmapId}/milestones/{milestoneId}/status?status=COMPLETED
     * Updates the status of a single milestone (Task module).
     * Ownership-checked: the milestone must belong to the caller's roadmap/project.
     */
    public MilestoneResponse updateMilestoneStatus(
            String email,
            Long milestoneId,
            MilestoneStatus newStatus) {

        StudentProfile profile = getProfileByEmail(email);

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Milestone",
                                "id",
                                milestoneId));

        // Navigate the lazy chain: Milestone → Roadmap → Project → StudentProfile
        // @Transactional on this method keeps the session alive for these traversals.
        Project project = milestone.getRoadmap().getProject();

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException(
                    "Access denied: this milestone does not belong to you.");
        }

        milestone.setStatus(newStatus);
        return toResponse(milestoneRepository.save(milestone));
    }
}