package com.studentguide.platform.exception;

/**
 * Thrown when a roadmap generation is requested for a project that already
 * has a roadmap persisted in the database.
 *
 * Mapped to 409 CONFLICT by GlobalExceptionHandler.
 * Regeneration (when intentionally replacing a roadmap) is a separate
 * operation and should use a dedicated endpoint.
 */
public class RoadmapAlreadyExistsException extends RuntimeException {

    public RoadmapAlreadyExistsException(Long projectId) {
        super("A roadmap already exists for project with id: " + projectId
                + ". Use the regenerate endpoint to replace it.");
    }
}
