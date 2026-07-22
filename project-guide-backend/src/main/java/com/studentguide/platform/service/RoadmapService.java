package com.studentguide.platform.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.RoadmapResponse;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final ProjectRepository projectRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final MilestoneRepository milestoneRepository;

    private StudentProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StudentProfile",
                        "userId",
                        user.getId()));
    }

    /**
     * Converts Roadmap entity to DTO.
     * Calculates the real completion percentage from milestones:
     *   completedMilestones / totalMilestones * 100
     * Returns 0.0 if the roadmap has no milestones yet.
     */
    private RoadmapResponse toResponse(Roadmap roadmap) {
        long total = milestoneRepository.countByRoadmapId(roadmap.getId());
        long completed = milestoneRepository.countByRoadmapIdAndStatus(
                roadmap.getId(), MilestoneStatus.COMPLETED);

        double progress = total == 0
                ? 0.0
                : Math.round((completed * 100.0 / total) * 10.0) / 10.0;

        return new RoadmapResponse(
                roadmap.getId(),
                roadmap.getProject().getId(),
                roadmap.getEstimatedDurationWeeks(),
                roadmap.getStatus(),
                roadmap.getGeneratedAt(),
                progress);
    }

    /**
     * GET /api/projects/{projectId}/roadmap
     * Returns the roadmap for a project.
     * Ownership-checked: the project must belong to the calling student.
     */
    public RoadmapResponse getRoadmap(String email, Long projectId) {
        StudentProfile profile = getProfileByEmail(email);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Project",
                        "id",
                        projectId));

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException(
                    "Access denied: this project does not belong to you.");
        }

        Roadmap roadmap = roadmapRepository.findByProject(project)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Roadmap",
                                "projectId",
                                projectId));

        return toResponse(roadmap);
    }
}
