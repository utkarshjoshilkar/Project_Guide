package com.studentguide.platform.service.ai;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.studentguide.platform.config.AiServiceConfig;
import com.studentguide.platform.dto.AIRequest;
import com.studentguide.platform.dto.AIResponse;
import com.studentguide.platform.dto.ai.AIRoadmapResponse;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.AIServiceException;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.exception.RoadmapAlreadyExistsException;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AIIntegrationService — Orchestration layer for AI roadmap generation.
 *
 * Responsibilities (and nothing more):
 *   1. Validate ownership: project must belong to the caller.
 *   2. Guard against duplicate roadmaps.
 *   3. Build the AIRequest from domain data.
 *   4. Call the FastAPI microservice via RestTemplate.
 *   5. Validate the AI response is non-null and has milestones.
 *   6. Delegate persistence to RoadmapPersistenceService.
 *   7. Return a confirmation AIResponse to the controller.
 *
 * FastAPI MUST NEVER write to the database — all persistence happens here
 * via RoadmapPersistenceService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AIIntegrationService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ProjectRepository projectRepository;
    private final RoadmapRepository roadmapRepository;
    private final RestTemplate restTemplate;
    private final AiServiceConfig aiServiceConfig;
    private final RoadmapPersistenceService roadmapPersistenceService;

    /**
     * POST /api/projects/{projectId}/generate-roadmap
     *
     * @param email      authenticated caller's email (from JWT)
     * @param projectId  the project for which to generate a roadmap
     * @return AIResponse confirmation with the new roadmapId
     */
    public AIResponse generateRoadmap(String email, Long projectId) {

        // ── 1. Resolve caller ─────────────────────────────────────────────────
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        StudentProfile profile = studentProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StudentProfile", "userId", user.getId()));

        // ── 2. Resolve and ownership-check the project ────────────────────────
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            throw new AccessDeniedException(
                    "Access denied: this project does not belong to you.");
        }

        // ── 3. Prevent duplicate roadmaps ─────────────────────────────────────
        if (roadmapRepository.findByProjectId(projectId).isPresent()) {
            throw new RoadmapAlreadyExistsException(projectId);
        }

        // ── 4. Build the AI request ───────────────────────────────────────────
        AIRequest aiRequest = buildAIRequest(project, profile);

        // ── 5. Call FastAPI ───────────────────────────────────────────────────
        AIRoadmapResponse aiResponse = callAIService(aiRequest);

        // ── 6. Validate the response ──────────────────────────────────────────
        validateAIResponse(aiResponse);

        // ── 7. Persist hierarchy via dedicated service ────────────────────────
        Roadmap roadmap = roadmapPersistenceService.persistRoadmap(project, aiResponse);

        log.info("Roadmap generation complete: roadmapId={}, projectId={}", roadmap.getId(), projectId);

        return new AIResponse(
                roadmap.getId(),
                projectId,
                "Roadmap generated successfully. " +
                        aiResponse.getMilestones().size() + " milestones created.",
                roadmap.getGeneratedAt());
    }

    // ────────────────────────────────────────────────────────────────────────
    // Private helpers
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Builds the AIRequest sent to FastAPI from the project and student profile.
     * Maps skills (comma-separated string) to a List<String>.
     */
    private AIRequest buildAIRequest(Project project, StudentProfile profile) {
        List<String> skills = profile.getSkills() != null
                ? Arrays.stream(profile.getSkills().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .toList()
                : List.of();

        // Derive a timeline string from the project deadline or default
        String timeline = project.getDeadline() != null
                ? project.getDeadline().toString()
                : "8 weeks";

        return AIRequest.builder()
                .projectId(project.getId())
                .projectName(project.getTitle())
                .projectDescription(project.getDescription())
                .branch(profile.getBranch())
                .year(String.valueOf(profile.getYear()))
                .experienceLevel(project.getSkillLevel())
                .currentSkills(skills)
                .timeline(timeline)
                .weeklyHours(20) // sensible default; can be user-configurable later
                .learningGoal(profile.getLearningGoal())
                .build();
    }

    /**
     * Calls the FastAPI /generate-roadmap endpoint.
     * Wraps all HTTP and connection errors into AIServiceException
     * so the caller gets a clean 502 BAD_GATEWAY response.
     */
    private AIRoadmapResponse callAIService(AIRequest aiRequest) {
        String url = aiServiceConfig.getAiServiceUrl() + "/generate-roadmap";

        log.info("Calling AI service at {} for project '{}'",
                url, aiRequest.getProjectName());

        try {
            AIRoadmapResponse response = restTemplate.postForObject(
                    url,
                    aiRequest,
                    AIRoadmapResponse.class);

            if (response == null) {
                throw new AIServiceException(
                        "AI service returned an empty response.");
            }

            return response;

        } catch (ResourceAccessException e) {
            // Connection refused / timeout — FastAPI is likely down
            log.error("AI service unreachable at {}: {}", url, e.getMessage());
            throw new AIServiceException(
                    "AI service is currently unavailable. Please try again later.", e);

        } catch (HttpClientErrorException e) {
            log.error("AI service returned client error {}: {}", e.getStatusCode(), e.getMessage());
            throw new AIServiceException(
                    "AI service rejected the request: " + e.getStatusText(), e);

        } catch (HttpServerErrorException e) {
            log.error("AI service returned server error {}: {}", e.getStatusCode(), e.getMessage());
            throw new AIServiceException(
                    "AI service encountered an internal error. Please try again.", e);
        }
    }

    /**
     * Validates that the AI response has the minimum required data.
     * Throws AIServiceException (502) if validation fails.
     */
    private void validateAIResponse(AIRoadmapResponse response) {
        if (response.getMilestones() == null || response.getMilestones().isEmpty()) {
            throw new AIServiceException(
                    "AI service returned a response with no milestones. Generation failed.");
        }
        if (response.getPhaseWiseLearningPlan() == null
                || response.getPhaseWiseLearningPlan().isEmpty()) {
            throw new AIServiceException(
                    "AI service returned a response with no learning phases. Generation failed.");
        }
    }
}
