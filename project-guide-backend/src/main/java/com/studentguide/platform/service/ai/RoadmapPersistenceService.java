package com.studentguide.platform.service.ai;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.ai.AIRoadmapPhase;
import com.studentguide.platform.dto.ai.AIRoadmapResponse;
import com.studentguide.platform.dto.ai.AIResource;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Resource;
import com.studentguide.platform.entity.ResourceType;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.RoadmapStatus;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.entity.TaskStatus;
import com.studentguide.platform.repository.MilestoneRepository;
import com.studentguide.platform.repository.ResourceRepository;
import com.studentguide.platform.repository.RoadmapRepository;
import com.studentguide.platform.repository.TaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * RoadmapPersistenceService
 *
 * Single Responsibility: converts an AI-generated roadmap response into
 * persisted database entities. It is the ONLY place where AI output
 * touches the database.
 *
 * Called exclusively by AIIntegrationService — never from controllers.
 *
 * Mapping strategy:
 *   AIRoadmapResponse.milestones[]              → Milestone entities
 *   phaseWiseLearningPlan[i].actionItems[]      → Task entities under milestone[i]
 *   AIRoadmapResponse.learningResources[]       → Resource entities (on milestone 0, task 0)
 *
 * The entire hierarchy is saved within a single @Transactional boundary.
 * Any failure triggers a full rollback — no partial data is ever persisted.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapPersistenceService {

    private final RoadmapRepository roadmapRepository;
    private final MilestoneRepository milestoneRepository;
    private final TaskRepository taskRepository;
    private final ResourceRepository resourceRepository;

    /**
     * Persists the full hierarchy: Roadmap → Milestones → Tasks → Resources.
     *
     * @param project       the owning project entity
     * @param aiResponse    the raw AI response to be mapped
     * @return the persisted Roadmap entity
     */
    @Transactional
    public Roadmap persistRoadmap(Project project, AIRoadmapResponse aiResponse) {

        // ── 1. Create and save the Roadmap ────────────────────────────────────
        int durationWeeks = parseDurationWeeks(aiResponse.getProjectSummary().getDuration());

        Roadmap roadmap = Roadmap.builder()
                .project(project)
                .estimatedDurationWeeks(durationWeeks)
                .status(RoadmapStatus.GENERATED)
                .build();

        roadmap = roadmapRepository.save(roadmap);
        log.info("Persisted Roadmap id={} for project id={}", roadmap.getId(), project.getId());

        // ── 2. Persist Milestones from AI milestones list ─────────────────────
        List<com.studentguide.platform.dto.ai.AIMilestone> aiMilestones =
                aiResponse.getMilestones();

        List<AIRoadmapPhase> phases = aiResponse.getPhaseWiseLearningPlan();

        List<Milestone> savedMilestones = persistMilestones(roadmap, aiMilestones);

        // ── 3. Persist Tasks from phases.actionItems under each milestone ─────
        persistTasksForMilestones(savedMilestones, phases);

        // ── 4. Persist roadmap-level learning resources on the first task ─────
        persistLearningResources(savedMilestones, aiResponse.getLearningResources());

        return roadmap;
    }

    // ────────────────────────────────────────────────────────────────────────
    // Step implementations
    // ────────────────────────────────────────────────────────────────────────

    private List<Milestone> persistMilestones(
            Roadmap roadmap,
            List<com.studentguide.platform.dto.ai.AIMilestone> aiMilestones) {

        return aiMilestones.stream()
                .map(aiMilestone -> {
                    Milestone milestone = Milestone.builder()
                            .roadmap(roadmap)
                            .title(aiMilestone.getTitle())
                            .description(aiMilestone.getDescription())
                            // sequence follows the AI's milestoneId (1-based)
                            .sequenceOrder(aiMilestone.getMilestoneId())
                            // Estimated 7 days per milestone by default;
                            // can be refined later via the update endpoint.
                            .estimatedDays(7)
                            .status(MilestoneStatus.NOT_STARTED)
                            .build();

                    return milestoneRepository.save(milestone);
                })
                .toList();
    }

    /**
     * Maps phase[i].actionItems → Tasks under milestone[i].
     * If there are more phases than milestones, extra phases are skipped.
     * If a phase has no action items, that milestone gets no tasks (handled gracefully).
     */
    private void persistTasksForMilestones(
            List<Milestone> milestones,
            List<AIRoadmapPhase> phases) {

        int limit = Math.min(milestones.size(), phases.size());

        for (int i = 0; i < limit; i++) {
            Milestone milestone = milestones.get(i);
            AIRoadmapPhase phase = phases.get(i);

            List<String> actionItems = phase.getActionItems();
            if (actionItems == null || actionItems.isEmpty()) {
                log.debug("Phase {} has no action items — milestone id={} will have no tasks",
                        i + 1, milestone.getId());
                continue;
            }

            for (String actionItem : actionItems) {
                Task task = Task.builder()
                        .milestone(milestone)
                        .title(actionItem)
                        // description enriched with phase objectives for context
                        .description(buildTaskDescription(phase))
                        .status(TaskStatus.TODO)
                        .build();

                taskRepository.save(task);
            }

            log.info("Persisted {} tasks for milestone id={}",
                    actionItems.size(), milestone.getId());
        }
    }

    /**
     * Attaches roadmap-level learning resources to the first task of the
     * first milestone. These are general-purpose materials that apply to the
     * whole roadmap rather than a single task.
     *
     * If no milestones or tasks exist yet, resources are skipped safely.
     */
    private void persistLearningResources(
            List<Milestone> milestones,
            List<AIResource> aiResources) {

        if (aiResources == null || aiResources.isEmpty()) return;
        if (milestones.isEmpty()) return;

        Milestone firstMilestone = milestones.get(0);
        List<Task> firstMilestoneTasks =
                taskRepository.findByMilestoneIdOrderByCreatedAtAsc(firstMilestone.getId());

        if (firstMilestoneTasks.isEmpty()) {
            log.debug("No tasks on milestone id={} — skipping resource attachment",
                    firstMilestone.getId());
            return;
        }

        Task anchorTask = firstMilestoneTasks.get(0);

        for (AIResource aiResource : aiResources) {
            Resource resource = Resource.builder()
                    .task(anchorTask)
                    .title(aiResource.getResourceName())
                    .url(aiResource.getUrl())
                    .type(mapResourceType(aiResource.getType()))
                    .description("AI-recommended learning resource")
                    .build();

            resourceRepository.save(resource);
        }

        log.info("Persisted {} learning resources on task id={}",
                aiResources.size(), anchorTask.getId());
    }

    // ────────────────────────────────────────────────────────────────────────
    // Helpers
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Parses duration strings like "8 weeks", "10 Weeks", "3 months" (≈4w/month).
     * Returns a safe default of 8 if parsing fails.
     */
    private int parseDurationWeeks(String duration) {
        if (duration == null || duration.isBlank()) return 8;
        try {
            String lower = duration.trim().toLowerCase();
            // Split on spaces and grab the numeric token
            String[] tokens = lower.split("\\s+");
            int number = Integer.parseInt(tokens[0]);
            if (lower.contains("month")) {
                return number * 4;
            }
            return number; // assumes weeks
        } catch (NumberFormatException e) {
            log.warn("Could not parse duration '{}', defaulting to 8 weeks", duration);
            return 8;
        }
    }

    /**
     * Maps AI type strings (case-insensitive) to ResourceType enum.
     * Defaults to ARTICLE for unrecognised types.
     */
    private ResourceType mapResourceType(String type) {
        if (type == null || type.isBlank()) return ResourceType.ARTICLE;
        return Arrays.stream(ResourceType.values())
                .filter(rt -> rt.name().equalsIgnoreCase(type.trim())
                        || type.trim().toLowerCase().contains(rt.name().toLowerCase()))
                .findFirst()
                .orElse(ResourceType.ARTICLE);
    }

    /**
     * Builds a rich task description from the phase's objectives.
     * Kept brief to avoid bloating the description field.
     */
    private String buildTaskDescription(AIRoadmapPhase phase) {
        if (phase.getObjectives() == null || phase.getObjectives().isEmpty()) {
            return "Phase: " + phase.getPhase();
        }
        String objectives = String.join("; ", phase.getObjectives());
        return "Phase: " + phase.getPhase() + " | Objectives: " + objectives;
    }
}
