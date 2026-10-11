package com.studentguide.platform.github.verification.service;

import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceStrength;
import com.studentguide.platform.github.evidence.model.FeatureEvidence;
import com.studentguide.platform.github.evidence.service.FeatureEvidenceService;
import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.verification.model.FeatureVerification;
import com.studentguide.platform.github.verification.model.ProgressReport;
import com.studentguide.platform.github.verification.model.VerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImplementationProgressVerificationService {

    private final FeatureEvidenceService featureEvidenceService;

    public ProgressReport verifyProgress(Project project, List<Task> allTasks, RepositorySummary summary) {
        List<FeatureVerification> verifications = new ArrayList<>();

        for (Task task : allTasks) {
            FeatureEvidence evidence = featureEvidenceService.evaluateFeature(task, summary);
            
            VerificationStatus status;
            String reasoning;
            
            switch (evidence.getOverallStrength()) {
                case STRONG:
                    status = VerificationStatus.IMPLEMENTED;
                    reasoning = "Strong evidence found indicating full implementation.";
                    break;
                case PARTIAL:
                    status = VerificationStatus.PARTIALLY_IMPLEMENTED;
                    reasoning = "Some evidence found, but implementation appears incomplete.";
                    break;
                case WEAK:
                    status = VerificationStatus.NOT_IMPLEMENTED;
                    reasoning = "Weak evidence found. Not enough to consider implemented.";
                    break;
                default:
                    status = VerificationStatus.INSUFFICIENT_EVIDENCE;
                    reasoning = "No relevant evidence found in the repository.";
            }

            verifications.add(FeatureVerification.builder()
                    .expectedTask(task)
                    .evidence(evidence)
                    .status(status)
                    .reasoning(reasoning)
                    .build());
        }

        return ProgressReport.builder()
                .projectId(project.getId())
                .featureVerifications(verifications)
                .build();
    }
}
