package com.studentguide.platform.github.guidance.service;

import com.studentguide.platform.entity.MilestoneStatus;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceSignal;
import com.studentguide.platform.github.guidance.model.AdaptiveGuidanceResponse;
import com.studentguide.platform.github.guidance.model.GuidanceAction;
import com.studentguide.platform.github.verification.model.FeatureVerification;
import com.studentguide.platform.github.verification.model.ProgressReport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdaptiveGuidanceService {

    public AdaptiveGuidanceResponse generateGuidance(ProgressReport report) {
        List<GuidanceAction> actions = new ArrayList<>();

        for (FeatureVerification verification : report.getFeatureVerifications()) {
            Task task = verification.getExpectedTask();
            String title = task.getTitle();
            
            if (task.getMilestone() != null && task.getMilestone().getStatus() == MilestoneStatus.COMPLETED) {
                continue; // Preserve completed milestones; only suggest changes to current/future tasks
            }
            if (task.getStatus() == com.studentguide.platform.entity.TaskStatus.DONE) {
                continue; // Preserve completed tasks
            }

            StringBuilder explanation = new StringBuilder();

            switch (verification.getStatus()) {
                case IMPLEMENTED:
                    explanation.append("Task is fully implemented. Next Step: Proceed to the next dependent task on your roadmap.");
                    appendSignals(explanation, verification.getEvidence().getSignals());
                    actions.add(new GuidanceAction(title, "PROCEED", explanation.toString()));
                    break;
                case PARTIALLY_IMPLEMENTED:
                    explanation.append("Task is partially implemented. Specific missing components need to be addressed.");
                    appendSignals(explanation, verification.getEvidence().getSignals());
                    actions.add(new GuidanceAction(title, "REVISE", explanation.toString()));
                    break;
                case NOT_IMPLEMENTED:
                case INSUFFICIENT_EVIDENCE:
                    explanation.append("No evidence found. Investigate the prerequisites or start implementing the core components.");
                    appendSignals(explanation, verification.getEvidence().getSignals());
                    actions.add(new GuidanceAction(title, "INVESTIGATE", explanation.toString()));
                    break;
            }
        }

        return AdaptiveGuidanceResponse.builder()
                .projectId(report.getProjectId())
                .recommendedActions(actions)
                .build();
    }
    
    private void appendSignals(StringBuilder explanation, List<EvidenceSignal> signals) {
        if (signals != null && !signals.isEmpty()) {
            explanation.append("\nEvidence Details:");
            for (EvidenceSignal signal : signals) {
                explanation.append("\n- ").append(signal.getDescription());
                if (signal.getFilePath() != null) {
                    explanation.append(" (Found in: ").append(signal.getFilePath()).append(")");
                }
            }
        }
    }
}
