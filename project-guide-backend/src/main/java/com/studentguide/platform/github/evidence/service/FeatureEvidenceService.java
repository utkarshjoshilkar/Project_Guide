package com.studentguide.platform.github.evidence.service;

import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceSignal;
import com.studentguide.platform.github.evidence.model.EvidenceStrength;
import com.studentguide.platform.github.evidence.model.FeatureEvidence;
import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FeatureEvidenceService {

    private static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
            "implement", "create", "add", "setup", "build", "write", "make",
            "test", "tests", "testing", "using", "with", "for", "in", "the", "a", "an", "and",
            "manager", "application", "service", "controller", "repository", "api", "data", "model",
            "basic", "simple", "complex", "feature", "system"
    ));

    public FeatureEvidence evaluateFeature(Task task, RepositorySummary summary) {
        List<EvidenceSignal> signals = new ArrayList<>();
        String title = task.getTitle().toLowerCase();
        
        if (title.contains("postman") || title.contains("install") || title.contains("download")) {
            signals.add(new EvidenceSignal(
                    "Task requires manual execution or external tools. Cannot be verified from source code alone.", 
                    "MANUAL_VERIFICATION_REQUIRED", null, EvidenceStrength.MISSING));
            return FeatureEvidence.builder()
                    .featureName(task.getTitle())
                    .signals(signals)
                    .overallStrength(EvidenceStrength.MISSING)
                    .build();
        }

        List<String> keywords = Arrays.stream(title.split("[^a-zA-Z0-9]+"))
                .filter(kw -> kw.length() > 3 && !STOP_WORDS.contains(kw))
                .collect(Collectors.toList());
        
        boolean isTestTask = title.contains("test") || title.contains("junit") || title.contains("mockito");
        
        int strongMatches = 0;
        int partialMatches = 0;

        if (summary.getArtifacts() != null && !keywords.isEmpty()) {
            for (ParsedArtifact artifact : summary.getArtifacts()) {
                String filePath = artifact.getFilePath();
                boolean isTestFile = filePath != null && (filePath.toLowerCase().contains("test") || filePath.toLowerCase().contains("spec"));
                
                if (isTestTask && !isTestFile) continue;
                if (!isTestTask && isTestFile) continue; // Prod code tasks should not be proven by test files alone
                
                int matchedKeywords = 0;

                if (artifact.getClasses() != null) {
                    for (String cls : artifact.getClasses()) {
                        String clsLower = cls.toLowerCase();
                        for (String kw : keywords) {
                            if (clsLower.contains(kw)) {
                                matchedKeywords++;
                                String msg = String.format("Class '%s' matches domain keyword '%s'.", cls, kw);
                                signals.add(new EvidenceSignal(msg, "CLASS_MATCH", filePath, EvidenceStrength.STRONG));
                            }
                        }
                    }
                }

                if (artifact.getMethods() != null) {
                    for (String mtd : artifact.getMethods()) {
                        String mtdLower = mtd.toLowerCase();
                        for (String kw : keywords) {
                            if (mtdLower.contains(kw)) {
                                matchedKeywords++;
                                String msg = String.format("Method '%s' matches domain keyword '%s'.", mtd, kw);
                                signals.add(new EvidenceSignal(msg, "METHOD_MATCH", filePath, EvidenceStrength.PARTIAL));
                            }
                        }
                    }
                }
                
                if (matchedKeywords >= keywords.size()) {
                    strongMatches++;
                } else if (matchedKeywords > 0) {
                    partialMatches++;
                }
            }
        }

        EvidenceStrength overall = EvidenceStrength.MISSING;
        if (keywords.isEmpty()) {
            signals.add(new EvidenceSignal("Task description is too generic to match specific source code elements.", "GENERIC_TASK", null, EvidenceStrength.MISSING));
        } else if (strongMatches > 0) {
            overall = EvidenceStrength.STRONG;
        } else if (partialMatches > 0) {
            overall = EvidenceStrength.PARTIAL;
        } else {
            signals.add(new EvidenceSignal("No relevant classes or methods found matching domain keywords: " + keywords, "NONE", null, EvidenceStrength.MISSING));
        }

        return FeatureEvidence.builder()
                .featureName(task.getTitle())
                .signals(signals)
                .overallStrength(overall)
                .build();
    }
}
