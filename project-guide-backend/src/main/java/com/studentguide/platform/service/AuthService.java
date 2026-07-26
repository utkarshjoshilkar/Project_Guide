package com.studentguide.platform.service;

import com.studentguide.platform.exception.InvalidCredentialsException;
import com.studentguide.platform.exception.UserAlreadyExistsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.studentguide.platform.dto.AuthResponse;
import com.studentguide.platform.dto.LoginRequest;
import com.studentguide.platform.dto.RegisterRequest;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.repository.UserRepository;
import com.studentguide.platform.security.JwtService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    /**
     * Registers a new student user.
     * createdAt is set automatically by @CreationTimestamp on the User entity.
     */
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            // Throw a proper exception — caught by GlobalExceptionHandler → 409 CONFLICT
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("STUDENT")
                // createdAt is managed automatically by @CreationTimestamp on the entity
                .build();

        userRepository.save(user);

        log.info("User registered: email={}", request.getEmail());
        return new AuthResponse("User registered successfully", null);
    }

    /**
     * Authenticates an existing user and returns a JWT token.
     */
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Failed login attempt for email={}", request.getEmail());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        log.info("User authenticated: email={}", request.getEmail());
        return new AuthResponse("Login Successful", token);
    }
}
