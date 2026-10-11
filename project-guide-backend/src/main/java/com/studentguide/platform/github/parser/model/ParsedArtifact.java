package com.studentguide.platform.github.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParsedArtifact {
    private String filePath;
    private String language;
    private List<String> classes;
    private List<String> methods;
    private List<String> imports;
}
