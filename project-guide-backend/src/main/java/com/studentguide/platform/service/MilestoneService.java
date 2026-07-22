package com.studentguide.platform.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.studentguide.platform.dto.MilestoneResponse;
import com.studentguide.platform.entity.Milestone;
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
     * Get all milestones for a roadmap.
     */
    public List<MilestoneResponse> getMilestones(
            String email,
            Long roadmapId) {

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

}