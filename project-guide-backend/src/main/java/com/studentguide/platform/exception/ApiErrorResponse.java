package com.studentguide.platform.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Standard error response body returned by GlobalExceptionHandler.
 * Every API error will have this consistent shape:
 * {
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "User not found with id: 5",
 *   "timestamp": "2026-06-15T00:39:38"
 * }
 */
@Getter
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
