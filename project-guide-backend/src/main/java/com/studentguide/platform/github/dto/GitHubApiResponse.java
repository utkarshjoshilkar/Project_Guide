package com.studentguide.platform.github.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubApiResponse {

    private String name;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("private")
    private Boolean privateRepository;

    @JsonProperty("default_branch")
    private String defaultBranch;
}

