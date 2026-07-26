package com.studentguide.platform.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.studentguide.platform.entity.Milestone;
import com.studentguide.platform.entity.Project;
import com.studentguide.platform.entity.Resource;
import com.studentguide.platform.entity.Roadmap;
import com.studentguide.platform.entity.StudentProfile;
import com.studentguide.platform.entity.Task;

import lombok.extern.slf4j.Slf4j;

/**
 * OwnershipValidator — single, shared component for enforcing resource ownership.
 *
 * Previously duplicated across:
 *   ProjectService (4 methods), RoadmapService (1), MilestoneService (2),
 *   TaskService (1 private helper), ResourceService (1 private helper),
 *   AIIntegrationService (1 inline check).
 *
 * All ownership checks reduce to one root question:
 *   "Does this resource ultimately belong to the given StudentProfile?"
 *
 * Entity ownership chain:
 *   Resource → Task → Milestone → Roadmap → Project → StudentProfile
 *
 * Each method delegates to the level above it, so the ownership comparison
 * logic lives in exactly ONE place: {@link #assertOwnsProject}.
 *
 * Why @Component and not @Service?
 *   @Service signals a class with business/domain logic that typically
 *   interacts with repositories. This class contains only assertion
 *   and access-control concerns — it is infrastructure, not business logic.
 *   @Component is the accurate stereotype.
 *
 * Why methods are void (assertions, not booleans)?
 *   Access checks either pass or throw. Returning boolean would force callers
 *   to write if-statements and choose their own exception type, splitting
 *   the responsibility back into the callers.
 */
@Slf4j
@Component
public class OwnershipValidator {

    /**
     * Core check: verifies that a Project belongs to the given StudentProfile.
     * All higher-level checks ultimately delegate here.
     *
     * @throws AccessDeniedException if the project does not belong to the profile
     */
    public void assertOwnsProject(StudentProfile profile, Project project) {
        if (!project.getStudentProfile().getId().equals(profile.getId())) {
            log.warn("Ownership violation: profileId={} attempted to access projectId={}",
                    profile.getId(), project.getId());
            throw new AccessDeniedException(
                    "Access denied: this project does not belong to you.");
        }
    }

    /**
     * Verifies that a Roadmap belongs to the profile, via its parent project.
     *
     * @throws AccessDeniedException if the roadmap's project does not belong to the profile
     */
    public void assertOwnsRoadmap(StudentProfile profile, Roadmap roadmap) {
        assertOwnsProject(profile, roadmap.getProject());
    }

    /**
     * Verifies that a Milestone belongs to the profile, via Roadmap → Project.
     * Requires an active JPA session (milestone.roadmap must not be uninitialized proxy).
     *
     * @throws AccessDeniedException if the milestone's project does not belong to the profile
     */
    public void assertOwnsMilestone(StudentProfile profile, Milestone milestone) {
        assertOwnsProject(profile, milestone.getRoadmap().getProject());
    }

    /**
     * Verifies that a Task belongs to the profile, via Milestone → Roadmap → Project.
     *
     * @throws AccessDeniedException if the task's project does not belong to the profile
     */
    public void assertOwnsTask(StudentProfile profile, Task task) {
        assertOwnsMilestone(profile, task.getMilestone());
    }

    /**
     * Verifies that a Resource belongs to the profile, via Task → Milestone → Roadmap → Project.
     *
     * @throws AccessDeniedException if the resource's project does not belong to the profile
     */
    public void assertOwnsResource(StudentProfile profile, Resource resource) {
        assertOwnsTask(profile, resource.getTask());
    }
}
