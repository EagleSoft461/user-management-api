package com.backend.usermanagement.controller;

import com.backend.usermanagement.dto.request.ForgotPasswordRequest;
import com.backend.usermanagement.dto.request.LoginRequest;
import com.backend.usermanagement.dto.request.RefreshTokenRequest;
import com.backend.usermanagement.dto.request.RegisterRequest;
import com.backend.usermanagement.dto.request.ResetPasswordRequest;
import com.backend.usermanagement.dto.response.AuthResponse;
import com.backend.usermanagement.dto.response.PasswordResetResponse;
import com.backend.usermanagement.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for login and registration")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot password", description = "Generates password reset token")
    public ResponseEntity<PasswordResetResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String token = authService.createPasswordResetToken(request.getEmail());
        // In production, token would be sent via email, not returned in response
        return ResponseEntity.ok(new PasswordResetResponse(
                "Password reset token generated. Check your email.", 
                token
        ));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Resets password using token")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword(), request.getConfirmPassword());
        return ResponseEntity.ok("Password reset successfully");
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Generates new access token using refresh token")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
