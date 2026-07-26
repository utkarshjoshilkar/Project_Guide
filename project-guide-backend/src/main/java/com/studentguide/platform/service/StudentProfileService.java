package com.studentguide.platform.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.studentguide.platform.dto.StudentProfileRequest;
import com.studentguide.platform.dto.StudentProfileResponse;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.exception.StudentProfileAlreadyExistsException;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * StudentProfileService — manages the student profile lifecycle.
 *
 * Refactored (Phase 2):
 *   - Private resolveUserId() helper removed; replaced by direct userRepository
 *     call inline in createProfile (ProfileResolver cannot be used there because
 *     the profile does not exist yet when creating it).
 *   - getProfile(), updateProfile(), deleteProfile() now use {@link ProfileResolver}
 *     to eliminate the repeated two-query pattern.
 *   - createProfile() switched from 10 setter calls to the @Builder pattern,
 *     consistent with every other service that creates entities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentProfileService {

    private final StudentProfileRepository studentProfileRepository;
    private final UserRepository userRepository;
    private final ProfileResolver profileResolver;

    /**
     * POST /api/student-profile
     * Creates a student profile for the authenticated user.
     *
     * Note: UserRepository is used directly here (not ProfileResolver) because
     * ProfileResolver would throw ResourceNotFoundException for a missing profile —
     * which is exactly the scenario we are handling (profile does not exist yet).
     */
    public StudentProfileResponse createProfile(String email, StudentProfileRequest request) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();

        if (studentProfileRepository.existsByUserId(userId)) {
            throw new StudentProfileAlreadyExistsException(userId);
        }

        StudentProfile profile = StudentProfile.builder()
                .userId(userId)
                .college(request.getCollege())
                .branch(request.getBranch())
                .year(request.getYear())
                .cgpa(request.getCgpa())
                .skills(request.getSkills())
                .interests(request.getInterests())
                .preferredTechStack(request.getPreferredTechStack())
                .githubProfile(request.getGithubProfile())
                .linkedinProfile(request.getLinkedinProfile())
                .learningGoal(request.getLearningGoal())
                .build();

        StudentProfile saved = studentProfileRepository.save(profile);
        log.info("StudentProfile created: profileId={}, userId={}", saved.getId(), userId);
        return mapToResponse(saved);
    }

    /**
     * GET /api/student-profile
     * Returns the profile for the authenticated user.
     */
    @Transactional(readOnly = true)
    public StudentProfileResponse getProfile(String email) {
        return mapToResponse(profileResolver.resolve(email));
    }

    /**
     * PUT /api/student-profile
     * Updates all mutable fields on the authenticated user's profile.
     */
    public StudentProfileResponse updateProfile(String email, StudentProfileRequest request) {
        StudentProfile profile = profileResolver.resolve(email);

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
     * DELETE /api/student-profile
     * Deletes the authenticated user's profile.
     */
    public void deleteProfile(String email) {
        StudentProfile profile = profileResolver.resolve(email);
        studentProfileRepository.delete(profile);
        log.info("StudentProfile deleted: profileId={}", profile.getId());
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