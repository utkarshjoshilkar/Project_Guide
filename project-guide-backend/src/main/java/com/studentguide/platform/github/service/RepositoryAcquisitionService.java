package com.studentguide.platform.github.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.studentguide.platform.github.dto.GitHubApiResponse;
import com.studentguide.platform.github.exception.GitHubRepositoryException;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RepositoryAcquisitionService {

    private final RestClient restClient;

    public GitHubApiResponse validateRepository(
            String owner,
            String repositoryName) {

        try {

            GitHubApiResponse response = restClient.get()
                    .uri("/repos/{owner}/{repositoryName}", owner, repositoryName)
                    .retrieve()
                    .body(GitHubApiResponse.class);

            if (Boolean.TRUE.equals(response.getPrivateRepository())) {
                throw new GitHubRepositoryException(
                        "Private GitHub repositories are not supported.");
            }

            return response;

        } catch (GitHubRepositoryException e) {

            throw e;

        } catch (Exception e) {

            throw new GitHubRepositoryException(
                    "Unable to validate GitHub repository: "
                    + owner + "/" + repositoryName);
        }
    }
}