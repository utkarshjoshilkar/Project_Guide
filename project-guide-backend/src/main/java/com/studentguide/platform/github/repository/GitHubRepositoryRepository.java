package com.studentguide.platform.github.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.github.entity.GitHubRepository;

public interface GitHubRepositoryRepository extends JpaRepository<GitHubRepository, Long> {
    Optional<GitHubRepository> findByProjectId(Long projectId);
    
}












