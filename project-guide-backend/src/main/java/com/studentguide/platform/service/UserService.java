package com.studentguide.platform.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.studentguide.platform.dto.UserResponse;
import com.studentguide.platform.dto.UserUpdateRequest;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.exception.ResourceNotFoundException;
import com.studentguide.platform.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ─────────────────────────────────────────────
    // Helper: Convert User entity → UserResponse DTO
    // Centralised here so every method reuses it.
    // ─────────────────────────────────────────────
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    // ─────────────────────────────────────────────
    // Helper: enforce ownership or ADMIN access.
    // Throws AccessDeniedException if the caller is not an ADMIN
    // and is trying to access a different user's resource.
    // ─────────────────────────────────────────────
    private void assertCallerCanAccess(Authentication authentication, Long targetId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            Long callerId = getUserIdByEmail(authentication.getName());
            if (!callerId.equals(targetId)) {
                throw new AccessDeniedException("You can only access your own profile.");
            }
        }
    }

    /**
     * GET /api/users
     * Returns all users as a list of UserResponse DTOs.
     * findAll() is provided for free by JpaRepository.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Resolves the authenticated user's numeric ID from their email.
     * Used internally for ownership checks without exposing
     * repository access outside the service layer.
     */
    public Long getUserIdByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email))
                .getId();
    }

    /**
     * GET /api/users/{id}
     * A user can fetch their own profile; ADMIN can fetch any profile.
     * Authorization logic lives here — not in the controller.
     */
    public UserResponse getUserById(Authentication authentication, Long id) {
        assertCallerCanAccess(authentication, id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return toResponse(user);
    }

    /**
     * PUT /api/users/{id}
     * Updates allowed fields and saves the entity back to the database.
     * A user can only update their own record; ADMIN can update any record.
     * Authorization logic lives here — not in the controller.
     */
    public UserResponse updateUser(Authentication authentication, Long id, UserUpdateRequest request) {
        assertCallerCanAccess(authentication, id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setFullName(request.getFullName());

        return toResponse(userRepository.save(user));
    }

    /**
     * DELETE /api/users/{id}
     * Verifies the user exists first, then deletes.
     * Throwing ResourceNotFoundException here prevents silent no-ops
     * when the client sends a non-existent ID.
     */
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }
}
