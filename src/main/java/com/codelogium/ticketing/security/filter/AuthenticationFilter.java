package com.codelogium.ticketing.security.filter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private CustomAuthenticationManager customAuthenticationManager;

    public AuthenticationFilter(CustomAuthenticationManager customAuthenticationManager) {
        this.customAuthenticationManager = customAuthenticationManager;
        // setFilterProcessesUrl is set in SecurityConfig
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        try {
            LoginRequest loginRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequest.class);

            // 3. Authenticate and return the result
            return customAuthenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

        } catch (IOException e) {
            // Catches JSON parsing errors
            throw new org.springframework.security.authentication.InternalAuthenticationServiceException(
                    "Failed to parse authentication request body.", e);
        }
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Authentication Failed\",\"message\":\"" + failed.getMessage() + "\"}");
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

            response.addHeader(SecurityConstants.AUTHORIZATION, SecurityConstants.BEARER + token);
            response.setContentType("application/json");
            response.getWriter().write("{\"token\":\"" + token + "\"}");
            response.getWriter().flush();

        } catch (Exception e) {
            // Fallback for JWT creation errors
            System.err.println("FATAL JWT CREATION ERROR: " + e.getMessage());
            e.printStackTrace();

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal Server Error: Failed to create token.\"}");
            response.getWriter().flush();
        }
    }
}