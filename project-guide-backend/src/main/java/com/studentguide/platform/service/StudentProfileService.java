package com.studentguide.platform.service;

import com.studentguide.platform.dto.StudentProfileRequest;
import com.studentguide.platform.dto.StudentProfileResponse;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository; // kept for user-existence validation

    public StudentProfileResponse createProfile(Long userId, StudentProfileRequest request) {

        // Validate the user exists before creating a profile
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (studentProfileRepository.existsByUserId(userId)) {
            throw new RuntimeException("Student profile already exists");
        }

        StudentProfile profile = new StudentProfile();

        profile.setUserId(userId);
        profile.setCollege(request.getCollege());
        profile.setBranch(request.getBranch());
        profile.setYear(request.getYear());
        profile.setCgpa(request.getCgpa());
        profile.setSkills(request.getSkills());
        profile.setInterests(request.getInterests());
        profile.setPreferredTechStack(request.getPreferredTechStack());
        profile.setGithubProfile(request.getGithubProfile());
        profile.setLinkedinProfile(request.getLinkedinProfile());
        profile.setLearningGoal(request.getLearningGoal());
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());

        StudentProfile savedProfile = studentProfileRepository.save(profile);

        return mapToResponse(savedProfile);
    }

    public StudentProfileResponse getProfile(Long userId) {

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        return mapToResponse(profile);
    }

    public StudentProfileResponse updateProfile(Long userId, StudentProfileRequest request) {

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        profile.setCollege(request.getCollege());
        profile.setBranch(request.getBranch());
        profile.setYear(request.getYear());
        profile.setCgpa(request.getCgpa());
        profile.setSkills(request.getSkills());
        profile.setInterests(request.getInterests());
        profile.setPreferredTechStack(request.getPreferredTechStack());
        profile.setGithubProfile(request.getGithubProfile());
        profile.setLinkedinProfile(request.getLinkedinProfile());
        profile.setLearningGoal(request.getLearningGoal());
        profile.setUpdatedAt(LocalDateTime.now());

        StudentProfile updatedProfile = studentProfileRepository.save(profile);

        return mapToResponse(updatedProfile);
    }

    public void deleteProfile(Long userId) {

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student profile not found"));

        studentProfileRepository.delete(profile);
    }

    private StudentProfileResponse mapToResponse(StudentProfile profile) {

        return new StudentProfileResponse(
                profile.getId(),
                profile.getCollege(),
                profile.getBranch(),
                profile.getYear(),
                profile.getCgpa(),
                profile.getSkills(),
                profile.getInterests(),
                profile.getPreferredTechStack(),
                profile.getGithubProfile(),
                profile.getLinkedinProfile(),
                profile.getLearningGoal());
    }
}