package com.studentguide.platform.service;

import java.util.List;

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

    /**
     * GET /api/users
     * Returns all users as a list of UserResponse DTOs.
     * findAll() is provided for free by JpaRepository.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse) // method reference — same as: user -> toResponse(user)
                .toList();
    }
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            
    /**
     * GET /api/users/{id}
     * Finds a user by ID, throws ResourceNotFoundException if not found.
     * orElseThrow() is how you safely unwrap an Optional in Spring.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return toResponse(user);
    }

    /**
     * PUT /api/users/{id}
     * Updates allowed fields and saves the entity back to the database.
     * Only fullName is updatable (email changes require verification).
     */
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setFullName(request.getFullName());

        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
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
