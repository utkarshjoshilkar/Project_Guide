package com.studentguide.platform.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Requires a valid JWT token (handled by JwtAuthFilter).
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users); // 200 OK
    }

    /**
     * GET /api/users/{id}
     * @PathVariable binds the {id} from the URL to the method parameter.
     * If user is not found, UserService throws ResourceNotFoundException
     * which GlobalExceptionHandler converts to 404.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user); // 200 OK
    }

    /**
     * PUT /api/users/{id}
     * @Valid triggers Bean Validation on UserUpdateRequest.
     * Validation failures are caught by GlobalExceptionHandler → 400 BAD REQUEST.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser); // 200 OK
    }

    /**
     * DELETE /api/users/{id}
     * Returns 204 NO CONTENT on success — standard REST convention.
     * 204 means "request succeeded, but there's no response body."
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build(); // 204 NO CONTENT
    }
}


 