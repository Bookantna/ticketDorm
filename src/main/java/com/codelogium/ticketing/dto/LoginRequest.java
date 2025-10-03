package com.codelogium.ticketing.dto;

public class LoginRequest {
    private String username;
    private String password;

    // Must have a default constructor for Jackson
    public LoginRequest() {}

    // Getters and Setters (or use Lombok's @Data)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}