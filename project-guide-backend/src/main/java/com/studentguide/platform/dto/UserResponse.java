package com.studentguide.platform.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Response DTO for User CRUD operations.
 *
 * IMPORTANT: This intentionally does NOT include the password field.
 * Never return passwords (even hashed) from an API response.
 */
@Getter
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
