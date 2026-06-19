package com.studentguide.platform.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import com.studentguide.platform.entity.User;
import com.studentguide.platform.repository.UserRepository;

import java.util.List;

/**
 * Spring Security requires a UserDetailsService to load users during
 * JWT token validation. This implementation loads users from the database
 * via UserRepository.
 *
 * Why is this separate from AuthService?
 * AuthService handles business logic (register, login).
 * UserDetailsService is a Spring Security contract — it's infrastructure,
 * not business logic. Keeping them separate follows single responsibility.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Convert our User entity into Spring Security's UserDetails.
        // SimpleGrantedAuthority wraps the role string (e.g. "STUDENT").
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
}
