package com.backend.usermanagement.dto.request;

import jakarta.validation.constraints.NotBlank;

public class DeactivateAccountRequest {

    @NotBlank(message = "Password is required to deactivate account")
    private String password;

    public DeactivateAccountRequest() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
