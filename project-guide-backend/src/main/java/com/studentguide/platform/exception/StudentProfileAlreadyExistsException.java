package com.studentguide.platform.exception;

public class StudentProfileAlreadyExistsException extends RuntimeException {
    public StudentProfileAlreadyExistsException(Long userId) {
        super("Student profile already exists for user with id: " + userId);
    }
}
