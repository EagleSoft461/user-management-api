package com.backend.usermanagement.dto.response;

import com.backend.usermanagement.domain.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class ProfileResponse {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String phoneNumber;
    private boolean active;
    private boolean emailVerified;
    private boolean twoFactorEnabled;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public ProfileResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.bio = user.getBio();
        this.phoneNumber = user.getPhoneNumber();
        this.active = user.isActive();
        this.emailVerified = user.isEmailVerified();
        this.twoFactorEnabled = user.isTwoFactorEnabled();
        this.createdAt = user.getCreatedAt();
        this.roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getBio() { return bio; }
    public String getPhoneNumber() { return phoneNumber; }
    public boolean isActive() { return active; }
    public boolean isEmailVerified() { return emailVerified; }
    public boolean isTwoFactorEnabled() { return twoFactorEnabled; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<String> getRoles() { return roles; }
}
