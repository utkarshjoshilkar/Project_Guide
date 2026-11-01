package com.studentguide.platform.github;

import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.github.evidence.model.EvidenceSignal;
import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.extractor.service.ProjectArtifactExtractorService;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import com.studentguide.platform.github.parser.service.CodeAnalysisService;
import com.studentguide.platform.github.parser.service.FastApiCodeAnalysisService;
import com.studentguide.platform.github.verification.model.FeatureVerification;
import com.studentguide.platform.github.verification.model.ProgressReport;
import com.studentguide.platform.github.verification.model.VerificationStatus;
import com.studentguide.platform.github.verification.service.ImplementationProgressVerificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;

/**
 * Integration test for the GitHub analysis pipeline.
 *
 * <p>Strategy:
 * <ul>
 *   <li>Uses {@code @TempDir} to create a deterministic, self-contained repository fixture
 *       — no external Git clone, no pre-existing tmpdir path required.</li>
 *   <li>Mocks {@link FastApiCodeAnalysisService} via {@code @MockitoBean} (Spring Boot 4 /
 *       Spring Framework 7 API) so no FastAPI process is needed in CI.</li>
 *   <li>The mock returns hand-crafted {@link ParsedArtifact} objects whose class and method
 *       names are chosen to exercise the exact keyword-matching logic in
 *       {@link com.studentguide.platform.github.evidence.service.FeatureEvidenceService}.</li>
 *   <li>All original assertions are preserved and unchanged.</li>
 * </ul>
 *
 * <h2>Keyword analysis</h2>
 * {@code FeatureEvidenceService} strips stop-words (length ≤ 3, or words like
 * "implement", "manager", "controller", "service", …) before matching. The relevant
 * surviving keywords per task are:
 * <ul>
 *   <li>"Implement Contact Manager" → keyword: {@code contact}</li>
 *   <li>"Test API endpoints using Postman" → short-circuits immediately via the
 *       {@code title.contains("postman")} guard — no keywords evaluated</li>
 *   <li>"Implement Owner Controller" → keyword: {@code owner}</li>
 * </ul>
 *
 * <h2>Fixture design</h2>
 * <ul>
 *   <li>{@code OwnerController.java} — class {@code OwnerController}: the keyword {@code owner}
 *       matches the class name → STRONG evidence → IMPLEMENTED ✓</li>
 *   <li>{@code PetClinicApplication.java} — class {@code PetClinicApplication}, method {@code main}:
 *       neither contains {@code contact} → confirms the false-positive fix still holds ✓</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class GitHubAnalyzerIntegrationTest {

    // ── @TempDir is resolved per-instance by JUnit 5 ─────────────────────────
    // Declared as a field so it is populated before @Test methods run.
    @TempDir
    Path repoDir;

    // ── FastApiCodeAnalysisService is replaced by a Mockito mock in the Spring
    // context. @MockitoBean is the Spring Boot 4 / Spring Framework 7 successor
    // to the deprecated @MockBean. It registers the mock as a Spring bean and
    // resets it between tests automatically.
    @MockitoBean
    private FastApiCodeAnalysisService fastApiCodeAnalysisService;

    @Autowired
    private CodeAnalysisService codeAnalysisService;

    @Autowired
    private ProjectArtifactExtractorService projectArtifactExtractorService;

    @Autowired
    private ImplementationProgressVerificationService implementationProgressVerificationService;

    // ── Fixture source code ───────────────────────────────────────────────────
    // Minimal, syntactically plausible Java that the scanner recognises as
    // language=JAVA and that our mocked FastAPI "parsed" correctly.

    private static final String OWNER_CONTROLLER_SOURCE =
            "package org.springframework.samples.petclinic.owner;\n" +
            "import org.springframework.stereotype.Controller;\n" +
            "import org.springframework.web.bind.annotation.GetMapping;\n" +
            "@Controller\n" +
            "public class OwnerController {\n" +
            "    @GetMapping(\"/owners\")\n" +
            "    public String listOwners() { return \"owners/ownersList\"; }\n" +
            "    @GetMapping(\"/owners/{ownerId}\")\n" +
            "    public String showOwner() { return \"owners/ownerDetails\"; }\n" +
            "}\n";

    private static final String PET_CLINIC_APPLICATION_SOURCE =
            "package org.springframework.samples.petclinic;\n" +
            "import org.springframework.boot.SpringApplication;\n" +
            "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
            "@SpringBootApplication\n" +
            "public class PetClinicApplication {\n" +
            "    public static void main(String[] args) {\n" +
            "        SpringApplication.run(PetClinicApplication.class, args);\n" +
            "    }\n" +
            "}\n";

    @Test
    void testSpringPetClinicAnalysis() throws IOException {

        // ── 1. Build a minimal repository fixture on disk ─────────────────────
        // RepositoryScannerService.scanRepository() only needs the root directory
        // to exist; it recursively walks .java files and passes each path+content
        // to FastApiCodeAnalysisService (which we mock below).

        Path srcMain = Files.createDirectories(
                repoDir.resolve("src/main/java/org/springframework/samples/petclinic"));
        Path ownerPkg = Files.createDirectories(
                repoDir.resolve("src/main/java/org/springframework/samples/petclinic/owner"));

        Path ownerControllerFile = ownerPkg.resolve("OwnerController.java");
        Path appFile             = srcMain.resolve("PetClinicApplication.java");

        Files.writeString(ownerControllerFile, OWNER_CONTROLLER_SOURCE);
        Files.writeString(appFile,             PET_CLINIC_APPLICATION_SOURCE);

        // Relative paths as they will appear in RepositoryFile.getPath()
        // (RepositoryScannerService uses rootDir.toPath().relativize(child.toPath()))
        String ownerControllerRelPath =
                repoDir.relativize(ownerControllerFile).toString();
        String appRelPath =
                repoDir.relativize(appFile).toString();

        // ── 2. Stub FastApiCodeAnalysisService ────────────────────────────────
        // Each stub is keyed on the file path substring so that when
        // CodeAnalysisService.analyzeRepository() iterates RepositoryFile entries
        // and calls fastApiCodeAnalysisService.analyzeJavaFile(path, source),
        // it receives a deterministic ParsedArtifact.

        // OwnerController — class name contains "owner" → keyword match for
        // "Implement Owner Controller" (keyword: "owner")
        ParsedArtifact ownerControllerArtifact = new ParsedArtifact(
                ownerControllerRelPath,
                "java",
                Collections.singletonList("OwnerController"),
                Arrays.asList("listOwners", "showOwner"),
                Arrays.asList(
                        "org.springframework.stereotype.Controller",
                        "org.springframework.web.bind.annotation.GetMapping"
                )
        );

        // PetClinicApplication — class name and method do NOT contain "contact"
        // → confirms the false-positive fix for "Implement Contact Manager"
        ParsedArtifact appArtifact = new ParsedArtifact(
                appRelPath,
                "java",
                Collections.singletonList("PetClinicApplication"),
                Collections.singletonList("main"),
                Arrays.asList(
                        "org.springframework.boot.SpringApplication",
                        "org.springframework.boot.autoconfigure.SpringBootApplication"
                )
        );

        // Stub: path contains "OwnerController" → return owner artifact
        when(fastApiCodeAnalysisService.analyzeJavaFile(
                contains("OwnerController"), anyString()))
                .thenReturn(ownerControllerArtifact);

        // Stub: path contains "PetClinicApplication" → return app artifact
        when(fastApiCodeAnalysisService.analyzeJavaFile(
                contains("PetClinicApplication"), anyString()))
                .thenReturn(appArtifact);

        // ── 3. Run the full analysis pipeline ─────────────────────────────────
        String repositoryPath = repoDir.toAbsolutePath().toString();
        System.out.println("Testing against fixture repository: " + repositoryPath);

        List<ParsedArtifact> artifacts = codeAnalysisService.analyzeRepository(repositoryPath);
        assertNotNull(artifacts, "analyzeRepository must not return null");
        assertFalse(artifacts.isEmpty(), "analyzeRepository must return at least one artifact");

        // ── 4. Extract artifact summary ───────────────────────────────────────
        RepositorySummary summary = projectArtifactExtractorService.extractArtifacts(artifacts);
        assertNotNull(summary);

        // ── 5. Set up tasks (identical to original test intent) ───────────────
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

        // ── 6. Verify progress ────────────────────────────────────────────────
        ProgressReport report = implementationProgressVerificationService
                .verifyProgress(mockProject, tasks, summary);

        // Diagnostic output (preserved from original)
        for (FeatureVerification v : report.getFeatureVerifications()) {
            System.out.println("TASK: " + v.getExpectedTask().getTitle());
            System.out.println("  -> STATUS: " + v.getStatus());
            System.out.println("  -> REASON: " + v.getReasoning());
            for (EvidenceSignal s : v.getEvidence().getSignals()) {
                System.out.println("     - [" + s.getSignalType() + "] "
                        + s.getDescription()
                        + " (File: " + s.getFilePath() + ")"
                        + " Strength: " + s.getStrength());
            }
        }

        // ── 7. Assertions (identical to original) ─────────────────────────────
        FeatureVerification contactV = report.getFeatureVerifications().get(0);
        assertEquals(VerificationStatus.INSUFFICIENT_EVIDENCE, contactV.getStatus(),
                "Contact Manager should be INSUFFICIENT_EVIDENCE, not IMPLEMENTED (false positive fix)");

        FeatureVerification postmanV = report.getFeatureVerifications().get(1);
        assertEquals(VerificationStatus.INSUFFICIENT_EVIDENCE, postmanV.getStatus(),
                "Postman task should be INSUFFICIENT_EVIDENCE (manual task check)");

        FeatureVerification ownerV = report.getFeatureVerifications().get(2);
        assertEquals(VerificationStatus.IMPLEMENTED, ownerV.getStatus(),
                "Owner Controller should be IMPLEMENTED because OwnerController class exists in fixture");
    }
}
