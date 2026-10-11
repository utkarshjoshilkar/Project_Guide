package com.studentguide.platform.github;

import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceSignal;
import com.studentguide.platform.github.evidence.model.FeatureEvidence;
import com.studentguide.platform.github.evidence.service.FeatureEvidenceService;
import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.extractor.service.ProjectArtifactExtractorService;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import com.studentguide.platform.github.parser.service.CodeAnalysisService;
import com.studentguide.platform.github.verification.model.FeatureVerification;
import com.studentguide.platform.github.verification.model.ProgressReport;
import com.studentguide.platform.github.verification.model.VerificationStatus;
import com.studentguide.platform.github.verification.service.ImplementationProgressVerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GitHubAnalyzerIntegrationTest {

    @Autowired
    private CodeAnalysisService codeAnalysisService;

    @Autowired
    private ProjectArtifactExtractorService projectArtifactExtractorService;

    @Autowired
    private FeatureEvidenceService featureEvidenceService;

    @Autowired
    private ImplementationProgressVerificationService implementationProgressVerificationService;

    @Test
    void testSpringPetClinicAnalysis() {
        String petClinicPath = System.getProperty("java.io.tmpdir") + "/project-guide/spring-projects/spring-petclinic";
        
        System.out.println("Testing against PetClinic path: " + petClinicPath);

        // 1. Parse/Analyze Repo
        List<ParsedArtifact> artifacts = codeAnalysisService.analyzeRepository(petClinicPath);
        assertNotNull(artifacts);
        assertFalse(artifacts.isEmpty());

        // 2. Extract Artifacts
        RepositorySummary summary = projectArtifactExtractorService.extractArtifacts(artifacts);
        assertNotNull(summary);
        
        // 3. Setup Mock Tasks
        Task contactManagerTask = new Task();
        contactManagerTask.setTitle("Implement Contact Manager");

        Task postmanTask = new Task();
        postmanTask.setTitle("Test API endpoints using Postman");
        
        Task ownerControllerTask = new Task();
        ownerControllerTask.setTitle("Implement Owner Controller");

        Project mockProject = new Project();
        mockProject.setId(999L);
        mockProject.setTitle("PetClinic Analysis Test");

        List<Task> tasks = Arrays.asList(contactManagerTask, postmanTask, ownerControllerTask);

        // 4. Verify Progress
        ProgressReport report = implementationProgressVerificationService.verifyProgress(mockProject, tasks, summary);
        
        // Output results for visual inspection in logs
        for (FeatureVerification v : report.getFeatureVerifications()) {
            System.out.println("TASK: " + v.getExpectedTask().getTitle());
            System.out.println("  -> STATUS: " + v.getStatus());
            System.out.println("  -> REASON: " + v.getReasoning());
            for (EvidenceSignal s : v.getEvidence().getSignals()) {
                System.out.println("     - [" + s.getSignalType() + "] " + s.getDescription() + " (File: " + s.getFilePath() + ") Strength: " + s.getStrength());
            }
        }

        // Assertions
        FeatureVerification contactV = report.getFeatureVerifications().get(0);
        assertEquals(VerificationStatus.INSUFFICIENT_EVIDENCE, contactV.getStatus(), 
            "Contact Manager should be INSUFFICIENT_EVIDENCE, not IMPLEMENTED (false positive fix)");

        FeatureVerification postmanV = report.getFeatureVerifications().get(1);
        assertEquals(VerificationStatus.INSUFFICIENT_EVIDENCE, postmanV.getStatus(), 
            "Postman task should be INSUFFICIENT_EVIDENCE (manual task check)");

        FeatureVerification ownerV = report.getFeatureVerifications().get(2);
        // "owner" and "controller" should match OwnerController in PetClinic
        assertEquals(VerificationStatus.IMPLEMENTED, ownerV.getStatus(), 
            "Owner Controller should be IMPLEMENTED because it actually exists in PetClinic");
    }
}
