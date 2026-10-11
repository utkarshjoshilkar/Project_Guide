package com.studentguide.platform.github.dto;

public class GitHubRepositoryRequest {

    private String repositoryUrl;

    public GitHubRepositoryRequest() {
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }
}