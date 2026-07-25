package com.studentguide.platform.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.studentguide.platform.dto.UserResponse;
import com.studentguide.platform.dto.UserUpdateRequest;
import com.studentguide.platform.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users
     * Returns the list of all registered users.
     * Restricted to ADMIN only — students must not see other users' data.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * GET /api/users/{id}
     * A user can fetch their own profile; ADMIN can fetch any profile.
     * Authorization logic is enforced inside UserService.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(userService.getUserById(authentication, id));
    }

    /**
     * PUT /api/users/{id}
     * A user can only update their own record. ADMIN can update any record.
     * Authorization logic is enforced inside UserService.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequest request) {

        return ResponseEntity.ok(userService.updateUser(authentication, id, request));
    }

    /**
     * DELETE /api/users/{id}
     * Hard-delete — restricted to ADMIN only.
     * Returns 204 NO CONTENT on success.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}