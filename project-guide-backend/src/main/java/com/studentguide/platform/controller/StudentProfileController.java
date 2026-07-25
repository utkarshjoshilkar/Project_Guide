package com.studentguide.platform.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.studentguide.platform.dto.StudentProfileRequest;
import com.studentguide.platform.dto.StudentProfileResponse;
import com.studentguide.platform.service.StudentProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/student-profile")
public class StudentProfileController {

    private final StudentProfileService studentProfileService;

    @PostMapping
    public ResponseEntity<StudentProfileResponse> createStudentProfile(
            Authentication authentication,
            @Valid @RequestBody StudentProfileRequest request) {

        StudentProfileResponse response =
                studentProfileService.createProfile(authentication.getName(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(Authentication authentication) {

        StudentProfileResponse response =
                studentProfileService.getProfile(authentication.getName());

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            Authentication authentication,
            @Valid @RequestBody StudentProfileRequest request) {

        StudentProfileResponse response =
                studentProfileService.updateProfile(authentication.getName(), request);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteProfile(Authentication authentication) {

        studentProfileService.deleteProfile(authentication.getName());

        return ResponseEntity.noContent().build();
    }
}
