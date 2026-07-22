package com.studentguide.platform.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.studentguide.platform.dto.RoadmapResponse;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoadmapService {

    private final RoadmapRepository roadmapRepository;
    private final ProjectRepository projectRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    private StudentProfile getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StudentProfile",
                        "userId",
                        user.getId()));
    }

    private RoadmapResponse toResponse(Roadmap roadmap) {
        return new RoadmapResponse(
                roadmap.getId(),
                roadmap.getProject().getId(),
                roadmap.getEstimatedDurationWeeks(),
                roadmap.getStatus(),
                roadmap.getGeneratedAt(),
                0.0);
    }

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
