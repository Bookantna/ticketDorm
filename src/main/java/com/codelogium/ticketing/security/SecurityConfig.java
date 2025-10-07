package com.codelogium.ticketing.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

        // Explicitly set the filter URL (matches the form action in the HTML)
        authenticationFilter.setFilterProcessesUrl("/users/authenticate");

        // --- CRUCIAL NOTE FOR FORM LOGIN ERROR ---
        // If your custom AuthenticationFilter is failing to parse the request body,
        // it means the filter is trying to read a JSON payload (using request.getInputStream())
        // but the HTML form is sending URL-encoded data (using request.getParameter()).
        // You MUST update your AuthenticationFilter implementation to read
        // parameters using request.getParameter("username") and request.getParameter("password")
        // for form submissions, or ensure your frontend always sends JSON.
        // ------------------------------------------

        http
                .headers(headers -> headers.disable())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize

                        // --- Public API Paths ---
                        .requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/**").permitAll()

                        // --- Public MVC Paths (Crucial for forms) ---
                        // Allow GET for showing the registration form
                        .requestMatchers(HttpMethod.GET, "/register").permitAll()
                        // Allow POST for submitting the registration form
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()

                        // Allow GET for showing the login form
                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                        // Allow POST for submitting the login form
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()

                        // Allow access to the welcome page after login
                        .requestMatchers(HttpMethod.GET, "/welcome").permitAll()
                        .requestMatchers(HttpMethod.GET, "/tickets/list").permitAll()//hasAnyAuthority("RENTER", "STAFF", "MECHANIC")
                        .requestMatchers(HttpMethod.GET, "/tickets/create").permitAll()//.hasAnyAuthority("RENTER")

                        // Path used internally by the AuthenticationFilter
                        .requestMatchers(HttpMethod.POST, "/users/authenticate").permitAll()


                        // --- Error handling and documentation paths ---
                        .requestMatchers("/error").permitAll()
                        // Fixed Swagger/API Docs paths for broader coverage
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**").permitAll()


                        // --- Protected paths ---
                        // Note: These need to be combined or sequenced carefully if multiple roles access the same path
                        .requestMatchers(HttpMethod.PATCH, "/users/{userId}/tickets/{ticketId}/status").hasAnyAuthority("MECHANIC", "STAFF")
                        .requestMatchers("/users/{userId}/tickets/{ticketId}/info").hasAuthority("RENTER")

                        // All other requests require authentication
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
