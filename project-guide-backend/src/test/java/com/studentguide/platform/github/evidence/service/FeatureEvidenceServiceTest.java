package com.studentguide.platform.github.evidence.service;

import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceStrength;
import com.studentguide.platform.github.evidence.model.FeatureEvidence;
import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class FeatureEvidenceServiceTest {

    private FeatureEvidenceService service;

    @BeforeEach
    void setUp() {
        service = new FeatureEvidenceService();
    }

    @Test
    void testFalsePositive_ContactManager_ShouldNotMatchPetClinicApplication() {
        // Given
        Task task = new Task();
        task.setTitle("Implement Contact Manager");

        ParsedArtifact genericArtifact = new ParsedArtifact(
                "src/main/java/com/example/PetClinicApplication.java",
                "java",
                Arrays.asList("PetClinicApplication"),
                Arrays.asList("main", "managerSetup"),
                Collections.emptyList()
        );

        RepositorySummary summary = RepositorySummary.builder()
                .artifacts(Collections.singletonList(genericArtifact))
                .build();

        // When
        FeatureEvidence evidence = service.evaluateFeature(task, summary);

        // Then
        // "manager" is a stop word, so "contact" is the only keyword.
        // PetClinicApplication and managerSetup do not contain "contact".
        assertEquals(EvidenceStrength.MISSING, evidence.getOverallStrength());
    }

    @Test
    void testValidMatch_ContactManager_ShouldMatchContactRepository() {
        // Given
        Task task = new Task();
        task.setTitle("Implement Contact Manager");

        ParsedArtifact artifact = new ParsedArtifact(
                "src/main/java/com/example/ContactRepository.java",
                "java",
                Arrays.asList("ContactRepository"),
                Arrays.asList("saveContact", "findContact"),
                Collections.emptyList()
        );

        RepositorySummary summary = RepositorySummary.builder()
                .artifacts(Collections.singletonList(artifact))
                .build();

        // When
        FeatureEvidence evidence = service.evaluateFeature(task, summary);

        // Then
        // "contact" keyword matches both class and methods.
        assertEquals(EvidenceStrength.STRONG, evidence.getOverallStrength());
        assertFalse(evidence.getSignals().isEmpty());
    }

    @Test
    void testPostmanTask_ShouldReturnMissing() {
        // Given
        Task task = new Task();
        task.setTitle("Test API using Postman");

        RepositorySummary summary = RepositorySummary.builder().build();

        // When
        FeatureEvidence evidence = service.evaluateFeature(task, summary);

        // Then
        assertEquals(EvidenceStrength.MISSING, evidence.getOverallStrength());
        assertEquals("MANUAL_VERIFICATION_REQUIRED", evidence.getSignals().get(0).getSignalType());
    }

    @Test
    void testTestFilesDoNotSatisfyProdTasks() {
        // Given
        Task task = new Task();
        task.setTitle("Implement User Registration");

        ParsedArtifact artifact = new ParsedArtifact(
                "src/test/java/com/example/UserRegistrationTest.java",
                "java",
                Arrays.asList("UserRegistrationTest"),
                Arrays.asList("testRegister"),
                Collections.emptyList()
        );

        RepositorySummary summary = RepositorySummary.builder()
                .artifacts(Collections.singletonList(artifact))
                .build();

        // When
        FeatureEvidence evidence = service.evaluateFeature(task, summary);

        // Then
        assertEquals(EvidenceStrength.MISSING, evidence.getOverallStrength());
    }
}
