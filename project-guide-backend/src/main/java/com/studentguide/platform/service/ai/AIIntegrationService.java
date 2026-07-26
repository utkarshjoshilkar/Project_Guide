package com.studentguide.platform.service.ai;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
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
import com.studentguide.platform.exception.AIServiceException;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.exception.RoadmapAlreadyExistsException;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.service.OwnershipValidator;
import com.studentguide.platform.service.ProfileResolver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AIIntegrationService — Orchestration layer for AI roadmap generation.
 *
 * Responsibilities (strictly ordered — nothing more):
 *   1. Resolve the caller's StudentProfile.
 *   2. Resolve the Project and assert ownership.
 *   3. Guard against duplicate roadmaps (idempotency check).
 *   4. Build the AIRequest DTO from domain data.
 *   5. Call the FastAPI microservice (OUTSIDE any database transaction).
 *   6. Validate the AI response is non-null and has milestones.
 *   7. Delegate all persistence to RoadmapPersistenceService (@Transactional).
 *   8. Return a confirmation AIResponse to the controller.
 *
 * ── Transaction Architecture (Phase 2 fix) ─────────────────────────────────
 * The previous version was annotated @Transactional at the class level. This
 * caused an open DB connection to be held for the ENTIRE duration of the FastAPI
 * HTTP call (potentially 10–30 seconds). Under concurrent load, this would exhaust
 * the connection pool and cause cascading 500 errors.
 *
 * Correct architecture:
 *   - This class carries NO @Transactional annotation.
 *   - Steps 1–6 run without a transaction (reads are short, network call is long).
 *   - Step 7 (persistence) is transactional via RoadmapPersistenceService,
 *     which opens and commits its own transaction atomically.
 *
 * FastAPI MUST NEVER write to the database — all persistence happens in
 * RoadmapPersistenceService.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    private final ProjectRepository projectRepository;
    private final RoadmapRepository roadmapRepository;
    private final RestTemplate restTemplate;
    private final AiServiceConfig aiServiceConfig;
    private final RoadmapPersistenceService roadmapPersistenceService;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;

    /**
     * POST /api/projects/{projectId}/generate-roadmap
     *
     * Correct execution order (no transaction wraps the HTTP call):
     *   1. Resolve profile (short DB read)
     *   2. Resolve project and assert ownership (short DB read)
     *   3. Check for existing roadmap (short DB read)
     *   4. Build AIRequest (in-memory, no DB)
     *   5. Call FastAPI — long-running, NO transaction held
     *   6. Validate AI response (in-memory, no DB)
     *   7. Persist hierarchy via RoadmapPersistenceService (@Transactional, short DB writes)
     *
     * @param email     authenticated caller's email (from JWT)
     * @param projectId the project for which to generate a roadmap
     * @return AIResponse confirmation with the new roadmapId
     */
    public AIResponse generateRoadmap(String email, Long projectId) {

        // ── 1. Resolve caller ─────────────────────────────────────────────────
        StudentProfile profile = profileResolver.resolve(email);

        // ── 2. Resolve and ownership-check the project ────────────────────────
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);

        // ── 3. Prevent duplicate roadmaps ─────────────────────────────────────
        if (roadmapRepository.findByProjectId(projectId).isPresent()) {
            throw new RoadmapAlreadyExistsException(projectId);
        }

        // ── 4. Build the AI request ───────────────────────────────────────────
        AIRequest aiRequest = buildAIRequest(project, profile);

        // ── 5. Call FastAPI (NO database transaction held at this point) ──────
        AIRoadmapResponse aiResponse = callAIService(aiRequest);

        // ── 6. Validate the response ──────────────────────────────────────────
        validateAIResponse(aiResponse);

        // ── 7. Persist hierarchy — RoadmapPersistenceService opens @Transactional
        Roadmap roadmap = roadmapPersistenceService.persistRoadmap(project, aiResponse);

        log.info("Roadmap generation complete: roadmapId={}, projectId={}, milestones={}",
                roadmap.getId(), projectId, aiResponse.getMilestones().size());

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
                .weeklyHours(20) // sensible default; can be user-configurable in v2
                .learningGoal(profile.getLearningGoal())
                .build();
    }

    /**
     * Calls the FastAPI /generate-roadmap endpoint.
     * Wraps all HTTP and connection errors into AIServiceException (502 BAD_GATEWAY).
     *
     * RestTemplate is configured with connect + read timeouts in AiServiceConfig.
     */
    private AIRoadmapResponse callAIService(AIRequest aiRequest) {
        String url = aiServiceConfig.getAiServiceUrl() + "/generate-roadmap";

        log.info("Calling AI service: url={}, project='{}', profileBranch='{}'",
                url, aiRequest.getProjectName(), aiRequest.getBranch());

        try {
            AIRoadmapResponse response = restTemplate.postForObject(
                    url,
                    aiRequest,
                    AIRoadmapResponse.class);

            if (response == null) {
                throw new AIServiceException("AI service returned an empty (null) response.");
            }

            log.info("AI service responded: milestonesReceived={}", response.getMilestones().size());
            return response;

        } catch (ResourceAccessException e) {
            // Connection refused or read/connect timeout
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
     * Validates that the AI response contains the minimum required data.
     * Throws AIServiceException (502) if the response is structurally incomplete.
     */
    private void validateAIResponse(AIRoadmapResponse response) {
        if (response.getMilestones() == null || response.getMilestones().isEmpty()) {
            log.error("AI service returned a response with no milestones");
            throw new AIServiceException(
                    "AI service returned a response with no milestones. Generation failed.");
        }
        if (response.getPhaseWiseLearningPlan() == null
                || response.getPhaseWiseLearningPlan().isEmpty()) {
            log.error("AI service returned a response with no learning phases");
            throw new AIServiceException(
                    "AI service returned a response with no learning phases. Generation failed.");
        }
    }
}
