package com.studentguide.platform.github.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.github.dto.GitHubRepositoryRequest;
import com.studentguide.platform.github.dto.GitHubRepositoryResponse;
import com.studentguide.platform.github.service.GitHubRepositoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/projects")
public class GitHubRepositoryController {

    private final GitHubRepositoryService githubRepositoryService;

    @PostMapping("/{projectId}/github")
    public ResponseEntity<GitHubRepositoryResponse> connectRepository(
            Authentication authentication,
            @PathVariable Long projectId,
            @RequestBody GitHubRepositoryRequest request) {

        GitHubRepositoryResponse response =
                githubRepositoryService.connectRepository(
                        authentication.getName(),
                        projectId,
                        request);

        return ResponseEntity.ok(response);
    }
}