package com.studentguide.platform.service;

import org.springframework.stereotype.Service;

import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.StudentProfileRepository;
import com.studentguide.platform.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ProfileResolver — single, shared component for resolving the authenticated
 * caller's StudentProfile from their email address.
 *
 * Previously duplicated verbatim in 8 service classes:
 *   ProjectService, RoadmapService, MilestoneService, TaskService,
 *   ResourceService, AIIntegrationService, DashboardService, StudentProfileService.
 *
 * Centralising here ensures that any future change to user→profile resolution
 * (e.g., caching, soft-delete support, multi-tenancy) is applied in exactly
 * ONE place without touching domain services.
 *
 * Transaction note: callers are always inside their own @Transactional boundary,
 * which covers these reads via Spring's REQUIRED propagation. No explicit
 * transaction annotation is needed here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileResolver {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    /**
     * Resolves the authenticated caller's StudentProfile from their JWT email.
     *
     * @param email the authenticated caller's email (from Authentication.getName())
     * @return the caller's StudentProfile entity
     * @throws ResourceNotFoundException if the User or StudentProfile does not exist
     */
    public StudentProfile resolve(String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();

        return studentProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "StudentProfile", "userId", userId));
    }
}
