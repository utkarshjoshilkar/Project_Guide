package com.studentguide.platform.github.extractor.service;

import com.studentguide.platform.github.extractor.model.RepositorySummary;
import com.studentguide.platform.github.parser.model.ParsedArtifact;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectArtifactExtractorService {

    public RepositorySummary extractArtifacts(List<ParsedArtifact> artifacts) {
        if (artifacts == null) {
            artifacts = new ArrayList<>();
        }

        int totalFiles = artifacts.size();
        int totalClasses = 0;
        int totalMethods = 0;
        int totalImports = 0;

        for (ParsedArtifact artifact : artifacts) {
            if (artifact.getClasses() != null) {
                totalClasses += artifact.getClasses().size();
            }
            if (artifact.getMethods() != null) {
                totalMethods += artifact.getMethods().size();
            }
            if (artifact.getImports() != null) {
                totalImports += artifact.getImports().size();
            }
        }

        return RepositorySummary.builder()
                .totalFiles(totalFiles)
                .totalClasses(totalClasses)
                .totalMethods(totalMethods)
                .totalImports(totalImports)
                .artifacts(artifacts)
                .build();
    }
}
