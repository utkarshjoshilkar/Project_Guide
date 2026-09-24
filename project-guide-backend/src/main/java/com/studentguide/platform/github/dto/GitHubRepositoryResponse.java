package com.studentguide.platform.github.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitHubRepositoryResponse {

    private Long id;
    private Long projectId;
    private String repositoryUrl;
    private String owner;
    private String repositoryName;
    private String defaultBranch;
}