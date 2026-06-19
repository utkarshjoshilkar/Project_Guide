package com.studentguide.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Request DTO for PUT /api/users/{id}.
 *
 * Only fields that are allowed to be updated are included here.
 * Email is intentionally excluded — changing an email requires
 * additional verification flows (e.g., re-confirm email).
 */
@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;
}
