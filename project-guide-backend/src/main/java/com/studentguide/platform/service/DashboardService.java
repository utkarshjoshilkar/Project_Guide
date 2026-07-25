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
import com.studentguide.platform.dto.ProjectSummaryResponse;
import com.studentguide.platform.dto.StudentProfileResponse;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.TaskStatus;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.TaskRepository;
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
    private final TaskRepository taskRepository;
    private final ProgressService progressService;

    /**
     * GET /api/dashboard
     *
     * Returns a full summary for the authenticated student:
     * - Profile info (nullable if not yet created)
     * - Total project count and breakdown by status
     * - Overall progress % (average across projects that have a roadmap)
     * - Last 3 projects updated, for the "Recent Activity" section
     * - Per-project summary with task/milestone counts and roadmap progress
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
                .map(roadmap -> progressService.calculateRoadmapProgress(roadmap.getId()))
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

        // ── 7. Per-project summaries ─────────────────────────────────────────
        List<ProjectSummaryResponse> projectSummaries = projects.stream()
                .map(this::toProjectSummary)
                .collect(Collectors.toList());

        return new DashboardResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                profileResponse,
                totalProjects,
                projectsByStatus,
                overallProgress,
                recentProjects,
                projectSummaries);
    }

    // ────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Builds a ProjectSummaryResponse for one project.
     * If the project has no roadmap yet, all numeric fields are 0.
     */
    private ProjectSummaryResponse toProjectSummary(Project project) {

        Optional<Roadmap> roadmapOpt = roadmapRepository.findByProject(project);

        if (roadmapOpt.isEmpty()) {
            return new ProjectSummaryResponse(
                    project.getId(),
                    project.getTitle(),
                    project.getStatus(),
                    0.0,   // roadmapProgress
                    0,     // totalMilestones
                    0,     // completedMilestones
                    0L,    // totalTasks
                    0L,    // completedTasks
                    0L);   // pendingTasks
        }

        Roadmap roadmap = roadmapOpt.get();
        Long roadmapId = roadmap.getId();

        double roadmapProgress = progressService.calculateRoadmapProgress(roadmapId);

        int totalMilestones = (int) milestoneRepository.countByRoadmapId(roadmapId);
        int completedMilestones =
                (int) milestoneRepository.countByRoadmapIdAndStatus(roadmapId, MilestoneStatus.COMPLETED);

        long totalTasks = taskRepository.countByMilestoneRoadmapId(roadmapId);
        long completedTasks = taskRepository.countByMilestoneRoadmapIdAndStatus(roadmapId, TaskStatus.DONE);
        long pendingTasks = totalTasks - completedTasks;

        return new ProjectSummaryResponse(
                project.getId(),
                project.getTitle(),
                project.getStatus(),
                roadmapProgress,
                totalMilestones,
                completedMilestones,
                totalTasks,
                completedTasks,
                pendingTasks);
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
