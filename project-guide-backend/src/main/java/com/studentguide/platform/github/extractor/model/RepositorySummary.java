package com.studentguide.platform.github.extractor.model;

import com.studentguide.platform.github.parser.model.ParsedArtifact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RepositorySummary {
    private int totalFiles;
    private int totalClasses;
    private int totalMethods;
    private int totalImports;
    private List<ParsedArtifact> artifacts;
}
