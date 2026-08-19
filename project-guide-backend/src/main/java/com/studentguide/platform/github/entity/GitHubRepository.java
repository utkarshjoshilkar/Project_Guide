package com.studentguide.platform.github.entity;

import com.studentguide.platform.entity.Project;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class GitHubRepository {
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;


}
