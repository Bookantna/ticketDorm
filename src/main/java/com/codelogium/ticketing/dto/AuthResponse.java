package com.codelogium.ticketing.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO matching the expected response structure from the /user/authenticate endpoint.
 * Example: {"token": "jwt_string..."}
 */
@Data
@NoArgsConstructor
public class AuthResponse {
    private String token;

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}