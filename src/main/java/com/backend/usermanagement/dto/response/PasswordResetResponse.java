package com.backend.usermanagement.dto.response;

public class PasswordResetResponse {

    private String message;
    private String token; // For testing without email

    public PasswordResetResponse(String message, String token) {
        this.message = message;
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
