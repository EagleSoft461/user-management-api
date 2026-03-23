package com.backend.usermanagement.dto.response;

public class AuthResponse {

    private Long id;
    private String accessToken;
    private String refreshToken;
    private String email;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String accessToken, String refreshToken, String email, String message) {
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.message = message;
    }

    public AuthResponse(String accessToken, String refreshToken, String email, String message) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.email = email;
        this.message = message;
    }

    // Backward compatibility constructor
    public AuthResponse(String token, String email, String message) {
        this.accessToken = token;
        this.email = email;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Backward compatibility
    public String getToken() {
        return accessToken;
    }

    public void setToken(String token) {
        this.accessToken = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
