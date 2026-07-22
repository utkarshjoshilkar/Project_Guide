package com.studentguide.platform.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.DashboardResponse;
import com.studentguide.platform.dto.ProjectResponse;
import com.studentguide.platform.dto.StudentProfileResponse;
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
public class DashboardService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ProjectRepository projectRepository;
    private final RoadmapRepository roadmapRepository;
    private final MilestoneRepository milestoneRepository;

    /**
     * GET /api/dashboard
     *
     * Returns a full summary for the authenticated student:
     * - Profile info (nullable if not yet created)
     * - Total project count and breakdown by status
     * - Overall progress % (average across projects that have a roadmap)
     * - Last 3 projects updated, for the "Recent Activity" section
     */
    public DashboardResponse getDashboard(String email) {

        // ── 1. Resolve the authenticated user ──────────────────────────────
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // ── 2. Resolve their student profile (optional) ────────────────────
        Optional<StudentProfile> profileOpt =
                studentProfileRepository.findByUserId(user.getId());

        StudentProfileResponse profileResponse = profileOpt.map(this::mapProfile).orElse(null);

        // ── 3. Fetch all projects (empty if no profile yet) ─────────────────
        List<Project> projects = profileOpt
                .map(p -> projectRepository.findByStudentProfileId(p.getId()))
                .orElse(List.of());

        // ── 4. Stats ─────────────────────────────────────────────────────────
        int totalProjects = projects.size();

        Map<String, Long> projectsByStatus = projects.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatus().name(),
                        Collectors.counting()));

        // ── 5. Overall progress — average % across projects with a roadmap ──
        List<Double> progressValues = projects.stream()
                .map(project -> roadmapRepository.findByProject(project))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::calculateProgress)
                .collect(Collectors.toList());

        double overallProgress = progressValues.isEmpty()
                ? 0.0
                : Math.round(
                        progressValues.stream()
                                .mapToDouble(Double::doubleValue)
                                .average()
                                .orElse(0.0) * 10.0) / 10.0;

        // ── 6. Recent projects — last 3 by updatedAt ────────────────────────
        List<ProjectResponse> recentProjects = projects.stream()
                .sorted(Comparator.comparing(Project::getUpdatedAt).reversed())
                .limit(3)
                .map(this::toProjectResponse)
                .collect(Collectors.toList());

        return new DashboardResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                profileResponse,
                totalProjects,
                projectsByStatus,
                overallProgress,
                recentProjects);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Calculates the completion percentage of a roadmap based on its milestones.
     * Returns 0.0 if the roadmap has no milestones.
     */
    private double calculateProgress(Roadmap roadmap) {
        long total = milestoneRepository.countByRoadmapId(roadmap.getId());
        if (total == 0) return 0.0;
        long completed = milestoneRepository.countByRoadmapIdAndStatus(
                roadmap.getId(), MilestoneStatus.COMPLETED);
        return completed * 100.0 / total;
    }

    private StudentProfileResponse mapProfile(StudentProfile profile) {
        return new StudentProfileResponse(
                profile.getId(),
                profile.getCollege(),
                profile.getBranch(),
                profile.getYear(),
                profile.getCgpa(),
                profile.getSkills(),
                profile.getInterests(),
                profile.getPreferredTechStack(),
                profile.getGithubProfile(),
                profile.getLinkedinProfile(),
                profile.getLearningGoal());
    }

    private ProjectResponse toProjectResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getDomain(),
                project.getPreferredTechStack(),
                project.getSkillLevel(),
                project.getDeadline(),
                project.getExpectedOutcome(),
                project.getStatus(),
                project.getStudentProfile().getId(),
                project.getCreatedAt(),
                project.getUpdatedAt());
    }
}
