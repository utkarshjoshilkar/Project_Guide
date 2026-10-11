package com.studentguide.platform.github.guidance.service;

import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceSignal;
import com.studentguide.platform.github.evidence.model.EvidenceStrength;
import com.studentguide.platform.github.evidence.model.FeatureEvidence;
import com.studentguide.platform.github.guidance.model.AdaptiveGuidanceResponse;
import com.studentguide.platform.github.verification.model.FeatureVerification;
import com.studentguide.platform.github.verification.model.ProgressReport;
import com.studentguide.platform.github.verification.model.VerificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AdaptiveGuidanceServiceTest {

    private AdaptiveGuidanceService service;

    @BeforeEach
    void setUp() {
        service = new AdaptiveGuidanceService();
    }

    @Test
    void testGuidanceIgnoresCompletedMilestones() {
        Milestone completedMilestone = new Milestone();
        completedMilestone.setStatus(MilestoneStatus.COMPLETED);

        Task task = new Task();
        task.setTitle("Completed Task");
        task.setMilestone(completedMilestone);

        FeatureVerification verification = FeatureVerification.builder()
                .expectedTask(task)
                .status(VerificationStatus.IMPLEMENTED)
                .build();

        ProgressReport report = ProgressReport.builder()
                .projectId(1L)
                .featureVerifications(Collections.singletonList(verification))
                .build();

        AdaptiveGuidanceResponse response = service.generateGuidance(report);

        // Assert that no guidance actions were generated for completed milestone tasks
        assertTrue(response.getRecommendedActions().isEmpty());
    }

    @Test
    void testGuidanceIgnoresCompletedTasks() {
        Milestone inProgressMilestone = new Milestone();
        inProgressMilestone.setStatus(MilestoneStatus.IN_PROGRESS);

        Task task = new Task();
        task.setTitle("Completed Task");
        task.setMilestone(inProgressMilestone);
        task.setStatus(com.studentguide.platform.entity.TaskStatus.DONE);

        FeatureVerification verification = FeatureVerification.builder()
                .expectedTask(task)
                .status(VerificationStatus.IMPLEMENTED)
                .build();

        ProgressReport report = ProgressReport.builder()
                .projectId(1L)
                .featureVerifications(Collections.singletonList(verification))
                .build();

        AdaptiveGuidanceResponse response = service.generateGuidance(report);

        // Assert that no guidance actions were generated for completed tasks
        assertTrue(response.getRecommendedActions().isEmpty());
    }

    @Test
    void testGuidanceActionGeneration() {
        Milestone inProgressMilestone = new Milestone();
        inProgressMilestone.setStatus(MilestoneStatus.IN_PROGRESS);

        // Task 1: Implemented
        Task task1 = new Task();
        task1.setTitle("Task 1");
        task1.setMilestone(inProgressMilestone);
        EvidenceSignal signal1 = new EvidenceSignal("Found match", "CLASS_MATCH", "src/Test.java", EvidenceStrength.STRONG);
        FeatureEvidence evidence1 = FeatureEvidence.builder().signals(Collections.singletonList(signal1)).build();
        FeatureVerification v1 = FeatureVerification.builder().expectedTask(task1).status(VerificationStatus.IMPLEMENTED).evidence(evidence1).build();

        // Task 2: Partially Implemented
        Task task2 = new Task();
        task2.setTitle("Task 2");
        task2.setMilestone(inProgressMilestone);
        EvidenceSignal signal2 = new EvidenceSignal("Partial match", "METHOD_MATCH", null, EvidenceStrength.PARTIAL);
        FeatureEvidence evidence2 = FeatureEvidence.builder().signals(Collections.singletonList(signal2)).build();
        FeatureVerification v2 = FeatureVerification.builder().expectedTask(task2).status(VerificationStatus.PARTIALLY_IMPLEMENTED).evidence(evidence2).build();

        // Task 3: Insufficient Evidence
        Task task3 = new Task();
        task3.setTitle("Task 3");
        task3.setMilestone(inProgressMilestone);
        EvidenceSignal signal3 = new EvidenceSignal("No match", "NONE", null, EvidenceStrength.MISSING);
        FeatureEvidence evidence3 = FeatureEvidence.builder().signals(Collections.singletonList(signal3)).build();
        FeatureVerification v3 = FeatureVerification.builder().expectedTask(task3).status(VerificationStatus.INSUFFICIENT_EVIDENCE).evidence(evidence3).build();

        ProgressReport report = ProgressReport.builder()
                .projectId(1L)
                .featureVerifications(Arrays.asList(v1, v2, v3))
                .build();

        AdaptiveGuidanceResponse response = service.generateGuidance(report);

        assertEquals(3, response.getRecommendedActions().size());

        // Assert Task 1
        assertEquals("PROCEED", response.getRecommendedActions().get(0).getActionType());
        assertTrue(response.getRecommendedActions().get(0).getExplanation().contains("Task is fully implemented"));
        assertTrue(response.getRecommendedActions().get(0).getExplanation().contains("Found match (Found in: src/Test.java)"));

        // Assert Task 2
        assertEquals("REVISE", response.getRecommendedActions().get(1).getActionType());
        assertTrue(response.getRecommendedActions().get(1).getExplanation().contains("Task is partially implemented"));
        assertTrue(response.getRecommendedActions().get(1).getExplanation().contains("Partial match"));

        // Assert Task 3
        assertEquals("INVESTIGATE", response.getRecommendedActions().get(2).getActionType());
        assertTrue(response.getRecommendedActions().get(2).getExplanation().contains("No evidence found"));
        assertTrue(response.getRecommendedActions().get(2).getExplanation().contains("No match"));
    }
}
