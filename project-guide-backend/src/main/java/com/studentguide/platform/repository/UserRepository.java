package com.studentguide.platform.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.studentguide.platform.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Spring Data JPA auto-generates the SQL for this from the method name:
     * SELECT * FROM users WHERE LOWER(full_name) LIKE LOWER('%name%')
     *
     * No @Query annotation needed — this is the power of Spring Data derived queries.
     */
    List<User> findByFullNameContainingIgnoreCase(String name);
}
