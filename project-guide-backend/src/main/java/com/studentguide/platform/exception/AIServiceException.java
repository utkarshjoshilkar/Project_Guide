package com.studentguide.platform.exception;

/**
 * Thrown when the AI microservice (FastAPI) call fails,
 * returns an unexpected status code, or returns an invalid response body.
 *
 * Mapped to 502 BAD_GATEWAY by GlobalExceptionHandler —
 * the failure is in an upstream dependency, not in our application.
 */
public class AIServiceException extends RuntimeException {

    public AIServiceException(String message) {
        super(message);
    }

    public AIServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
