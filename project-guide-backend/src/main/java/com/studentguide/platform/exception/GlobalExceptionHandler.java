package com.studentguide.platform.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.studentguide.platform.exception.AIServiceException;
import com.studentguide.platform.exception.RoadmapAlreadyExistsException;

/**
 * Centralized exception handler for the entire application.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 *                       It intercepts exceptions thrown from
 *                       any @RestController and converts
 *                       them into a structured ApiErrorResponse instead of a
 *                       raw stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles: POST /api/projects/{id}/generate-roadmap when FastAPI is unreachable
     * or returns an invalid response.
     * Returns: 502 BAD GATEWAY
     */
    @ExceptionHandler(AIServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleAIServiceException(AIServiceException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_GATEWAY.value(),
                "Bad Gateway",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(error);
    }

    /**
     * Handles: POST /api/projects/{id}/generate-roadmap when roadmap already exists.
     * Returns: 409 CONFLICT
     */
    @ExceptionHandler(RoadmapAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRoadmapAlreadyExists(RoadmapAlreadyExistsException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles: GET /users/999 when user with id=999 does not exist.
     * Returns: 404 NOT FOUND
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles: POST /auth/register when email is already registered.
     * Returns: 409 CONFLICT
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles: @Valid failures on @RequestBody (e.g. blank email, missing
     * password).
     * Collects all field-level violations into one message.
     * Returns: 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        // Collect all field errors: "email: Email should be valid, password: Password
        // is required"
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                errorMessage,
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles: POST /auth/login with wrong credentials.
     * Returns: 401 UNAUTHORIZED
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handles: POST /student-profile when a profile already exists for the user.
     * Returns: 409 CONFLICT
     */
    @ExceptionHandler(StudentProfileAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleStudentProfileAlreadyExists(StudentProfileAlreadyExistsException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles: any unexpected exception not caught by the handlers above.
     * Returns: 500 INTERNAL SERVER ERROR
     *
     * Note: In production, you would log ex.getMessage() here instead of
     * exposing raw exception messages to clients.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ApiErrorResponse error = new ApiErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

}
