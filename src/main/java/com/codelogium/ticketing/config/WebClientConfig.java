package com.codelogium.ticketing.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configures the WebClient used by the AuthService to call the local REST API endpoints.
 */
@Configuration
public class WebClientConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        // Base URL is set to call the REST API on the same running server instance
        String baseUrl = "http://localhost:" + serverPort;
        return builder.baseUrl(baseUrl).build();
    }
}
