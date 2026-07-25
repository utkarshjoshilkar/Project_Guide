package com.studentguide.platform.service;

import com.studentguide.platform.dto.StudentProfileRequest;
import com.studentguide.platform.dto.StudentProfileResponse;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.exception.StudentProfileAlreadyExistsException;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;

    // ─────────────────────────────────────────────
    // Helper: resolve email → userId
    // Keeps repository access out of the controller layer.
    // ─────────────────────────────────────────────
    private Long resolveUserId(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();
    }

    /**
     * Creates a student profile for the authenticated user.
     * Throws if the user does not exist or a profile already exists.
     */
    public StudentProfileResponse createProfile(String email, StudentProfileRequest request) {

        Long userId = resolveUserId(email);

        if (studentProfileRepository.existsByUserId(userId)) {
            throw new StudentProfileAlreadyExistsException(userId);
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
        // createdAt and updatedAt are set automatically by @CreationTimestamp / @UpdateTimestamp

        return mapToResponse(studentProfileRepository.save(profile));
    }

    /**
     * Returns the profile for the authenticated user.
     */
    public StudentProfileResponse getProfile(String email) {

        Long userId = resolveUserId(email);

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", userId));

        return mapToResponse(profile);
    }

    /**
     * Updates all mutable fields on the authenticated user's profile.
     */
    public StudentProfileResponse updateProfile(String email, StudentProfileRequest request) {

        Long userId = resolveUserId(email);

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", userId));

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
        // updatedAt refreshed automatically by @UpdateTimestamp

        return mapToResponse(studentProfileRepository.save(profile));
    }

    /**
     * Deletes the authenticated user's profile.
     */
    public void deleteProfile(String email) {

        Long userId = resolveUserId(email);

        StudentProfile profile = studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("StudentProfile", "userId", userId));

        studentProfileRepository.delete(profile);
    }

    // ─────────────────────────────────────────────
    // Helper: StudentProfile entity → DTO
    // ─────────────────────────────────────────────
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