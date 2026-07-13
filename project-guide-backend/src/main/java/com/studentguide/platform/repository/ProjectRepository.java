package com.studentguide.platform.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.ProjectStatus;
import com.studentguide.platform.entity.StudentProfile;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    List<Project> findByStudentProfile(StudentProfile studentProfile);
    
    List<Project> findByStudentProfileId(Long studentProfileId);

    List<Project> findByStatus(ProjectStatus status);

    List<Project> findByStudentProfileIdAndStatus(Long studentProfileId,ProjectStatus status);
}


