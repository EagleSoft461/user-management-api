package com.backend.usermanagement.dto.response;

import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse {

    private Long id;
    private String email;
    private boolean active;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, boolean active, LocalDateTime createdAt, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.active = active;
        this.createdAt = createdAt;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
