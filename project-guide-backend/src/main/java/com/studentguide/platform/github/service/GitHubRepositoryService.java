package com.studentguide.platform.github.service;

import java.net.URI;

import org.springframework.stereotype.Service;

import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.github.dto.GitHubApiResponse;
import com.studentguide.platform.github.dto.GitHubRepositoryRequest;
import com.studentguide.platform.github.dto.GitHubRepositoryResponse;
import com.studentguide.platform.github.entity.GitHubRepository;
import com.studentguide.platform.github.exception.GitHubRepositoryException;
import com.studentguide.platform.github.repository.GitHubRepositoryRepository;
import com.studentguide.platform.repository.ProjectRepository;
import com.studentguide.platform.service.OwnershipValidator;
import com.studentguide.platform.service.ProfileResolver;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GitHubRepositoryService {

    private final GitHubRepositoryRepository repository;
    private final ProjectRepository projectRepository;
    private final ProfileResolver profileResolver;
    private final OwnershipValidator ownershipValidator;
    private final RepositoryAcquisitionService acquisitionService;

    public GitHubRepositoryResponse connectRepository(
            String username,
            Long projectId,
            GitHubRepositoryRequest request) {

        StudentProfile profile = profileResolver.resolve(username);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Project", "id", projectId));

        ownershipValidator.assertOwnsProject(profile, project);

        validateGitHubUrl(request.getRepositoryUrl());

        String[] repositoryDetails =
                extractRepositoryDetails(request.getRepositoryUrl());

        String owner = repositoryDetails[0];
        String repositoryName = repositoryDetails[1];

        GitHubApiResponse githubResponse =
                acquisitionService.validateRepository(
                        owner,
                        repositoryName);

        GitHubRepository githubRepository =
                new GitHubRepository();

        githubRepository.setProject(project);
        githubRepository.setRepositoryUrl(request.getRepositoryUrl());
        githubRepository.setOwner(owner);
        githubRepository.setRepositoryName(repositoryName);
        githubRepository.setDefaultBranch(
                githubResponse.getDefaultBranch());

        repository.save(githubRepository);

        return new GitHubRepositoryResponse(
            githubRepository.getId(),
            project.getId(),
            githubRepository.getRepositoryUrl(),
            githubRepository.getOwner(),
            githubRepository.getRepositoryName(),
            githubRepository.getDefaultBranch()
        );
    }

    private String[] extractRepositoryDetails(String repositoryUrl) {

        String[] parts = repositoryUrl
                .replace("https://github.com/", "")
                .replace("http://github.com/", "")
                .split("/");

        if (parts.length < 2) {
            throw new GitHubRepositoryException(
                    "Invalid GitHub repository URL.");
        }

        return new String[] {
                parts[0],
                parts[1]
        };
    }

    private void validateGitHubUrl(String repositoryUrl) {

        try {

            URI uri = URI.create(repositoryUrl);

            if (!"https".equalsIgnoreCase(uri.getScheme())
                    || !"github.com".equalsIgnoreCase(uri.getHost())) {

                throw new GitHubRepositoryException(
                        "Invalid GitHub repository URL");
            }

        } catch (IllegalArgumentException e) {

            throw new GitHubRepositoryException(
                    "Invalid GitHub repository URL");
        }
    }
}