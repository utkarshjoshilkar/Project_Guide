package com.studentguide.platform.github.controller;

import com.studentguide.platform.github.dto.GitHubRepositoryRequest;
import com.studentguide.platform.github.dto.GitHubRepositoryResponse;
import com.studentguide.platform.github.scanner.model.RepositoryFile;
import com.studentguide.platform.github.scanner.service.RepositoryScannerService;
import com.studentguide.platform.github.service.GitHubRepositoryService;
import com.studentguide.platform.github.parser.service.CodeAnalysisService;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.extractor.service.ProjectArtifactExtractorService;
import com.studentguide.platform.github.verification.model.ProgressReport;
import com.studentguide.platform.github.verification.service.ImplementationProgressVerificationService;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.Task;
import com.studentguide.platform.repository.ProjectRepository;

import java.io.File;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class GitHubRepositoryController {

    private final GitHubRepositoryService githubRepositoryService;
    private final RepositoryScannerService repositoryScannerService;
    private final CodeAnalysisService codeAnalysisService;
    private final ProjectArtifactExtractorService projectArtifactExtractorService;
    private final ImplementationProgressVerificationService implementationProgressVerificationService;
    private final com.studentguide.platform.github.guidance.service.AdaptiveGuidanceService adaptiveGuidanceService;
    private final ProjectRepository projectRepository;


    // Connect GitHub repository
    @PostMapping("/{projectId}/github")
    public ResponseEntity<GitHubRepositoryResponse> connectRepository(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody GitHubRepositoryRequest request) {

        GitHubRepositoryResponse response =
                githubRepositoryService.connectRepository(
                        authentication.getName(),
                        projectId,
                        request
                );

        return ResponseEntity.ok(response);
    }


    // Scan repository
    @GetMapping("/{projectId}/github/scan")
    public ResponseEntity<List<RepositoryFile>> scanRepository(
            Authentication authentication,
            @PathVariable Long projectId) {

        String localPath =
                githubRepositoryService.getRepositoryPath(
                        authentication.getName(),
                        projectId
                );

        List<RepositoryFile> files =
                repositoryScannerService.scanRepository(localPath);

        return ResponseEntity.ok(files);
    }


    // Analyze repository
    @GetMapping("/{projectId}/github/analyze")
    public ResponseEntity<List<ParsedArtifact>> analyzeRepository(
            Authentication authentication,
            @PathVariable Long projectId) {

        String localPath =
                githubRepositoryService.getRepositoryPath(
                        authentication.getName(),
                        projectId
                );

        List<ParsedArtifact> files =
                codeAnalysisService.analyzeRepository(localPath);

        return ResponseEntity.ok(files);
    }


    // Acquire / clone repository
    @PostMapping("/{projectId}/github/acquire")
    public ResponseEntity<String> acquireRepository(
            Authentication authentication,
            @PathVariable Long projectId) {

        String localPath =
                githubRepositoryService.acquireRepository(
                        authentication.getName(),
                        projectId
                );

        return ResponseEntity.ok(localPath);
    }

    // Verify repository implementation progress
    @Transactional(readOnly = true)
    @GetMapping("/{projectId}/github/verify")
    public ResponseEntity<ProgressReport> verifyRepositoryProgress(
            Authentication authentication,
            @PathVariable Long projectId) {

        String localPath = githubRepositoryService.getRepositoryPath(authentication.getName(), projectId);

        File dir = new File(localPath);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new RuntimeException("Repository not found locally. Please acquire it first.");
        }

        List<ParsedArtifact> artifacts = codeAnalysisService.analyzeRepository(localPath);
        RepositorySummary summary = projectArtifactExtractorService.extractArtifacts(artifacts);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        List<Task> allTasks = new ArrayList<>();
        if (project.getRoadmap() != null && project.getRoadmap().getMilestones() != null) {
            for (Milestone milestone : project.getRoadmap().getMilestones()) {
                if (milestone.getTasks() != null) {
                    milestone.getTasks().forEach(task -> {
                        allTasks.add(task);
                        if (task.getResources() != null) {
                            task.getResources().size(); // Force initialization
                        }
                    });
                }
            }
        }

        ProgressReport report = implementationProgressVerificationService.verifyProgress(project, allTasks, summary);
        return ResponseEntity.ok(report);
    }

    // Generate adaptive guidance based on implementation progress
    @Transactional(readOnly = true)
    @GetMapping("/{projectId}/github/guidance")
    public ResponseEntity<com.studentguide.platform.github.guidance.model.AdaptiveGuidanceResponse> getGuidance(
            Authentication authentication,
            @PathVariable Long projectId) {

        ResponseEntity<ProgressReport> verifyResponse = verifyRepositoryProgress(authentication, projectId);
        ProgressReport report = verifyResponse.getBody();

        com.studentguide.platform.github.guidance.model.AdaptiveGuidanceResponse guidance = 
            adaptiveGuidanceService.generateGuidance(report);
        
        return ResponseEntity.ok(guidance);
    }
}

