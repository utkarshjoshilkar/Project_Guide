package com.studentguide.platform.github.parser.service;

import com.studentguide.platform.github.scanner.model.RepositoryFile;
import com.studentguide.platform.github.scanner.service.RepositoryScannerService;
import com.studentguide.platform.github.parser.model.ParsedArtifact;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeAnalysisService {

    private final RepositoryScannerService scannerService;
    private final FastApiCodeAnalysisService fastApiCodeAnalysisService;

    public List<ParsedArtifact> analyzeRepository(String repositoryPath) {

        List<RepositoryFile> files = scannerService.scanRepository(repositoryPath);
        
        List<RepositoryFile> javaFiles = files.stream()
                .filter(file -> !file.isDirectory() && "JAVA".equalsIgnoreCase(file.getLanguage()))
                .toList();
                
        log.info("Discovered {} Java files in repository {}", javaFiles.size(), repositoryPath);

        List<ParsedArtifact> artifacts = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (RepositoryFile file : javaFiles) {
            try {
                File localFile = new File(repositoryPath, file.getPath());
                if (!localFile.exists()) {
                    log.warn("File not found on disk: {}", localFile.getAbsolutePath());
                    failCount++;
                    continue;
                }
                
                String sourceCode = Files.readString(localFile.toPath());
                ParsedArtifact artifact = fastApiCodeAnalysisService.analyzeJavaFile(file.getPath(), sourceCode);
                
                if (artifact != null) {
                    artifacts.add(artifact);
                    successCount++;
                }
            } catch (Exception e) {
                log.error("Failed to analyze Java file: {}", file.getPath(), e);
                failCount++;
            }
        }
        
        log.info("Analysis complete for {}: {} succeeded, {} failed out of {} total Java files",
                 repositoryPath, successCount, failCount, javaFiles.size());

        return artifacts;
    }
}