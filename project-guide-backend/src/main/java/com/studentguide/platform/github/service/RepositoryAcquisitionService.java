package com.studentguide.platform.github.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;

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

    public String acquireRepository(
        String owner,
        String repositoryName,
        String branch) {

    try {
        String repositoryUrl =
                "https://github.com/" + owner + "/" + repositoryName + ".git";

        String localDirectory =
                System.getProperty("java.io.tmpdir")
                        + "/project-guide/"
                        + owner
                        + "/"
                        + repositoryName;

        java.io.File directory =
                new java.io.File(localDirectory);

        if (directory.exists()) {
            return localDirectory;
        }

        Git.cloneRepository()
                .setURI(repositoryUrl)
                .setBranch(branch)
                .setDirectory(directory)
                .call()
                .close();

        return localDirectory;

    } catch (Exception e) {
        throw new GitHubRepositoryException(
                "Unable to acquire GitHub repository: "
                        + owner + "/" + repositoryName);
    }
}

}