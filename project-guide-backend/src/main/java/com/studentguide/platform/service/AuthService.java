package com.studentguide.platform.service;

import java.time.LocalDateTime;

import com.studentguide.platform.exception.UserAlreadyExistsException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.studentguide.platform.dto.AuthResponse;
import com.studentguide.platform.dto.LoginRequest;
import com.studentguide.platform.dto.RegisterRequest;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.repository.UserRepository;
import com.studentguide.platform.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;


    public AuthResponse register(RegisterRequest request){

        if (userRepository.existsByEmail(request.getEmail())) {
            // Throw a proper exception — caught by GlobalExceptionHandler → 409 CONFLICT
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("STUDENT")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new AuthResponse("User registered successfully", null);
    }

    public AuthResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(()-> new RuntimeException("Invalid email or password"));
        
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(),user.getPassword());

        if(!passwordMatches){
            throw new RuntimeException( "Invalid email or password");
        }

        String token = jwtService.generateToken(user);
        
        return new AuthResponse("Login Successful", token);
    }
        

}
