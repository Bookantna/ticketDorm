package com.codelogium.ticketing.security.filter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.codelogium.ticketing.dto.LoginRequest;
import com.codelogium.ticketing.security.SecurityConstants;
import com.codelogium.ticketing.security.manager.CustomAuthenticationManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private CustomAuthenticationManager customAuthenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthenticationFilter(CustomAuthenticationManager customAuthenticationManager) {
        this.customAuthenticationManager = customAuthenticationManager;
        // setFilterProcessesUrl is set in SecurityConfig
        // Must set AuthenticationManager to null here to prevent superclass from interfering
        super.setAuthenticationManager(null);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        String username = null;
        String password = null;

        // 1. Attempt to read credentials from standard form parameters first (for HTML forms)
        username = request.getParameter("username");
        password = request.getParameter("password");

        // 2. If form parameters are null, attempt to read the body as JSON (for REST API clients)
        if (username == null || password == null) {
            try {
                // Note: reading body can only be done once
                LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
                username = loginRequest.getUsername();
                password = loginRequest.getPassword();
            } catch (IOException e) {
                // Throws an exception if the body cannot be parsed as JSON.
                throw new org.springframework.security.authentication.InternalAuthenticationServiceException(
                        "Failed to parse authentication request body. Ensure data is either URL-encoded or valid JSON.", e);
            }
        }

        // 3. Final check and authentication attempt
        if (username == null || password == null) {
            throw new org.springframework.security.authentication.BadCredentialsException(
                    "Missing username or password in authentication request.");
        }

        return customAuthenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        // Ensure the message is retrieved safely
        String message = failed.getMessage() != null ? failed.getMessage() : "Bad credentials.";
        response.getWriter().write("{\"error\":\"Authentication Failed\",\"message\":\"" + message + "\"}");
        response.getWriter().flush();
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        try {
            List<String> authorities = authResult.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            String token = JWT.create()
                    .withSubject(authResult.getName())
                    .withClaim("authorities", authorities)
                    .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.TOKEN_EXPIRATION))
                    .sign(Algorithm.HMAC512(SecurityConstants.SECRET_KEY));

            // --- CONDITIONAL REDIRECTION/RESPONSE LOGIC START ---

            // Check if the client expects a JSON response (typical for API clients)
            String acceptHeader = request.getHeader("Accept");
            boolean expectsJson = acceptHeader != null && acceptHeader.contains("application/json");
            String userId = authResult.getName(); // Assuming subject/name is the user identifier

            if (expectsJson) {
                // Scenario 2: API Client (Return JWT and User ID)
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");

                // Build the JSON response containing the user ID and token
                Map<String, Object> responseBody = Map.of(
                        "userId", userId,
                        "token", token,
                        "message", "Authentication successful. Use the token for subsequent API calls."
                );

                // Write JSON response using the existing ObjectMapper
                response.getWriter().write(objectMapper.writeValueAsString(responseBody));
                response.getWriter().flush();

            } else {
                // Scenario 1: Web Client (Set Cookie and Redirect to Welcome Page)

                // Get username from authentication result
                String userName = authResult.getName();

                // 1. Store the JWT in a secure, HTTP-only cookie.
                Cookie jwtCookie = new Cookie("JWT_TOKEN", token);
                jwtCookie.setHttpOnly(true);
                jwtCookie.setPath("/");
                jwtCookie.setMaxAge((int) (SecurityConstants.TOKEN_EXPIRATION / 1000));
                response.addCookie(jwtCookie);

                // 2. Add Username Cookie (Client-readable for UI)
                Cookie usernameCookie = new Cookie("USER_NAME", userName);
                usernameCookie.setHttpOnly(false); // Client-side JS access is allowed for UI display
                usernameCookie.setPath("/");
                usernameCookie.setMaxAge((int) (SecurityConstants.TOKEN_EXPIRATION / 1000));
                response.addCookie(usernameCookie);

                // 3. Add Roles Cookie (Client-readable for UI role checks)
                String rolesString = authorities.stream().collect(Collectors.joining(","));
                Cookie roleCookie = new Cookie("USER_ROLES", rolesString);
                roleCookie.setHttpOnly(false); // Client-side JS access is allowed
                roleCookie.setPath("/");
                roleCookie.setMaxAge((int) (SecurityConstants.TOKEN_EXPIRATION / 1000));
                response.addCookie(roleCookie);


                // 4. Redirect the user to the welcome/index page.
                response.sendRedirect("/welcome");
            }

            // --- CONDITIONAL REDIRECTION/RESPONSE LOGIC END ---

        } catch (Exception e) {
            System.err.println("FATAL JWT/Response ERROR: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Internal Server Error\",\"message\":\"Failed during token creation or response handling.\"}");
            response.getWriter().flush();
        }
    }
}
