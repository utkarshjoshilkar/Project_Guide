package com.studentguide.platform.github.scanner.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RepositoryFile {

    private String path;
    private String fileName;
    private String extension;
    private long size;
    private boolean directory;
    private String language;
}