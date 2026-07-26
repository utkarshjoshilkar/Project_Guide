package com.studentguide.platform.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT Authentication Filter — runs exactly once per HTTP request.
 *
 * Flow:
 * 1. Read the "Authorization" header
 * 2. Extract the token from "Bearer <token>"
 * 3. Validate the token and extract the user's email
 * 4. Load user from database
 * 5. Set authentication in SecurityContextHolder
 * 6. Pass request to the next filter in the chain
 *
 * If ANY step fails (missing header, expired token, user not found),
 * the filter simply passes the request through without setting authentication.
 * Spring Security will then reject the request with 401 Unauthorized.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Step 1: Get the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // Step 2: If header is missing or doesn't start with "Bearer ", skip this filter
        // This is normal for public endpoints like /api/auth/register
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token (everything after "Bearer ")
        final String jwt = authHeader.substring(7);

        try {
            // Step 4: Extract the email (subject) from the token
            final String email = jwtService.extractSubject(jwt);

            // Step 5: Only authenticate if we got an email AND there's no existing auth
            // SecurityContextHolder.getContext().getAuthentication() == null means
            // this request hasn't been authenticated yet in this request cycle
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Step 6: Load the user from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Step 7: Create the authentication token Spring Security uses internally
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,              // credentials — null because JWT already verified identity
                        userDetails.getAuthorities()
                );

                // Step 8: Attach request details (IP address, session ID) to the auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Step 9: Store authentication in SecurityContextHolder
                // This is what tells Spring Security "this request is authenticated"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (ExpiredJwtException e) {
            // Log expired tokens as WARN — this is a normal, expected scenario (not an attack)
            log.warn("JWT token expired for request [{}]: {}",
                    request.getRequestURI(), e.getMessage());

        } catch (SignatureException e) {
            // Tampered token — this IS suspicious and worth monitoring
            log.warn("JWT signature validation failed for request [{}] — possible tampering",
                    request.getRequestURI());

        } catch (MalformedJwtException e) {
            log.warn("Malformed JWT for request [{}]: {}",
                    request.getRequestURI(), e.getMessage());

        } catch (Exception e) {
            // Unexpected error (e.g. DB error in loadUserByUsername)
            log.error("Unexpected error during JWT validation for request [{}]: {} — {}",
                    request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
        }

        // Step 10: Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}

