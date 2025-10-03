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
                        .requestMatchers(HttpMethod.POST, "/users").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/authenticate").permitAll()
                        .requestMatchers("/swagger-ui/*", "/api-docs/**", "/h2-console/*").permitAll()
                        // Protected paths
                        .requestMatchers(HttpMethod.PATCH, "/users/{userId}/tickets/{ticketId}/status").hasAuthority("IT_SUPPORT")
                        .requestMatchers("/users/{userId}/tickets/{ticketId}/info").hasAuthority("EMPLOYEE")
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