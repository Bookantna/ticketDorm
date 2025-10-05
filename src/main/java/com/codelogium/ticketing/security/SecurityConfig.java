package com.codelogium.ticketing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Added
import org.springframework.security.web.SecurityFilterChain;

import com.codelogium.ticketing.security.filter.AuthenticationFilter;
import com.codelogium.ticketing.security.filter.ExceptionHandlerFilter;
import com.codelogium.ticketing.security.filter.JWTAuthorizationFilter;
import com.codelogium.ticketing.security.handler.CustomAccessDeniedHandler;
import com.codelogium.ticketing.security.handler.CustomAuthenticationEntryPoint;
import com.codelogium.ticketing.security.manager.CustomAuthenticationManager;

import lombok.AllArgsConstructor;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    @Autowired
    CustomAuthenticationManager customAuthenticationManager;

    @Bean // <-- REQUIRED BEAN DEFINITION
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);

        // Explicitly set the filter URL
        authenticationFilter.setFilterProcessesUrl("/user/authenticate");

        http
                .headers(headers -> headers.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Public paths
                        .requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()

                        // FIX: Use wildcard to permit ALL POST requests under /api/users (e.g., /register)
                        .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()

                        // Whitelist paths that are part of the root path, just in case
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/authenticate").permitAll()

                        // Error handling and documentation paths
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/*", "/api-docs/**", "/h2-console/*").permitAll()

                        // Protected paths
                        .requestMatchers(HttpMethod.PATCH, "/users/{userId}/tickets/{ticketId}/status").hasAuthority("MECHANIC")
                        .requestMatchers(HttpMethod.PATCH, "/users/{userId}/tickets/{ticketId}/status").hasAuthority("STAFF")
                        .requestMatchers("/users/{userId}/tickets/{ticketId}/info").hasAuthority("RENTER")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(handler -> {
                    handler.accessDeniedHandler(new CustomAccessDeniedHandler());
                    handler.authenticationEntryPoint(new CustomAuthenticationEntryPoint());
                })
                // Filters
                .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                .addFilter(authenticationFilter)
                .addFilterAfter(new JWTAuthorizationFilter(), AuthenticationFilter.class)
                // Stateless session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
