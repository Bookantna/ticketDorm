package com.codelogium.ticketing.service;

import com.codelogium.ticketing.dto.AuthResponse;
import com.codelogium.ticketing.dto.LoginRequest;
import com.codelogium.ticketing.dto.UserRegistrationRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Service acting as a client to the internal REST API for authentication and registration.
 * This is used by the UserWebController to call the specific endpoints.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    // Manual logger implementation to fix 'cannot find symbol variable log'
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final WebClient webClient;

    private static final String REGISTER_URI = "/api/users/register";
    private static final String AUTHENTICATE_URI = "/user/authenticate";

    public AuthService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<String> registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user: {}", request.getUser().getUsername());

        try {
            // FIX: Use HttpStatusCode::isError for the predicate and rename inner variable to clientResponse
            String responseBody = webClient.post()
                    .uri(REGISTER_URI)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        HttpStatusCode status = clientResponse.statusCode();
                        log.error("Registration failed with status: {}", status);
                        return clientResponse.bodyToMono(String.class).flatMap(body ->
                                Mono.error(new RuntimeException("API Error: " + status + " - " + body))
                        );
                    })
                    .bodyToMono(String.class) // Expects success message string
                    .block();

            return Optional.ofNullable(responseBody);
        } catch (Exception e) {
            log.error("Critical error during user registration API call: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Calls the backend API to authenticate the user and retrieve the JWT token.
     * @param request The login credentials (LoginRequest DTO).
     * @return Optional AuthResponse containing the token string on success, or empty on failure.
     */
    public Optional<AuthResponse> authenticate(LoginRequest request) {
        log.info("Attempting to authenticate user: {}", request.getUsername());

        try {
            // FIX: Use HttpStatusCode::is4xxClientError for the predicate and rename inner variable to clientResponse
            AuthResponse authResponse = webClient.post()
                    .uri(AUTHENTICATE_URI)
                    .bodyValue(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                        log.warn("Authentication failed with status: {}", clientResponse.statusCode());
                        return Mono.error(new SecurityException("Invalid credentials or authentication failure."));
                    })
                    .bodyToMono(AuthResponse.class)
                    .block();

            return Optional.ofNullable(authResponse);

        } catch (Exception e) {
            log.error("Critical error during user authentication API call: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
