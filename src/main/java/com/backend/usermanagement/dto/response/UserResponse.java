package com.backend.usermanagement.dto.response;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private boolean active;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public UserResponse() {
    }

    public UserResponse(Long id, String email, boolean active, boolean emailVerified, LocalDateTime createdAt, Set<String> roles) {
        this.id = id;
        this.email = email;
        this.active = active;
        this.emailVerified = emailVerified;
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

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
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
