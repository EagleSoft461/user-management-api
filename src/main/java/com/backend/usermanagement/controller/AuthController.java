package com.backend.usermanagement.controller;

import com.backend.usermanagement.dto.request.ForgotPasswordRequest;
import com.backend.usermanagement.dto.request.EmailVerificationRequest;
import com.backend.usermanagement.dto.request.LoginRequest;
import com.backend.usermanagement.dto.request.RefreshTokenRequest;
import com.backend.usermanagement.dto.request.RegisterRequest;
import com.backend.usermanagement.dto.request.ResetPasswordRequest;
import com.backend.usermanagement.dto.request.TwoFactorLoginRequest;
import com.backend.usermanagement.dto.request.TwoFactorVerifyRequest;
import com.backend.usermanagement.dto.response.AuthResponse;
import com.backend.usermanagement.dto.response.PasswordResetResponse;
import com.backend.usermanagement.dto.response.TwoFactorSetupResponse;
import com.backend.usermanagement.service.AuthService;
import com.backend.usermanagement.service.RateLimitService;
import dev.samstevens.totp.exceptions.QrGenerationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for login and registration")
public class AuthController {

    private final AuthService authService;
    private final RateLimitService rateLimitService;

    public AuthController(AuthService authService, RateLimitService rateLimitService) {
        this.authService = authService;
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String clientKey = resolveClientKey(httpRequest) + ":" + request.getEmail().toLowerCase();
        rateLimitService.consumeTokenOrThrow(
                "auth:login:" + clientKey,
                5,
                5,
                java.time.Duration.ofMinutes(1),
                "Too many login attempts. Please try again in a minute."
        );
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verifies user email with verification token")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully");
    }

    @PostMapping("/resend-verification")
    @Operation(summary = "Resend verification token", description = "Generates a new email verification token")
    public ResponseEntity<String> resendVerification(@Valid @RequestBody EmailVerificationRequest request,
                                                     HttpServletRequest httpRequest) {
        String clientKey = resolveClientKey(httpRequest) + ":" + request.getEmail().toLowerCase();
        rateLimitService.consumeTokenOrThrow(
                "auth:resend-verification:" + clientKey,
                3,
                3,
                java.time.Duration.ofMinutes(5),
                "Too many verification email requests. Please try again later."
        );
        authService.resendVerificationToken(request.getEmail());
        return ResponseEntity.ok("Verification email sent successfully");
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

    // 2FA Setup: QR code ve secret döndür (JWT gerekli)
    @PostMapping("/2fa/setup")
    @Operation(summary = "Setup 2FA", description = "Generates QR code for Google Authenticator")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<TwoFactorSetupResponse> setupTwoFactor(
            @AuthenticationPrincipal UserDetails userDetails) throws QrGenerationException {
        TwoFactorSetupResponse response = authService.setupTwoFactor(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    // 2FA Verify: QR tarandıktan sonra kodu doğrula ve 2FA'yı aktif et
    @PostMapping("/2fa/verify")
    @Operation(summary = "Verify and enable 2FA", description = "Verifies TOTP code and enables 2FA")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> verifyTwoFactor(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TwoFactorVerifyRequest request) {
        authService.verifyTwoFactor(userDetails.getUsername(), request.getCode());
        return ResponseEntity.ok("2FA enabled successfully");
    }

    // 2FA Login: Email/şifre sonrası kod ile token al
    @PostMapping("/2fa/validate")
    @Operation(summary = "Validate 2FA code", description = "Validates TOTP code and returns JWT token")
    public ResponseEntity<AuthResponse> validateTwoFactor(
            @Valid @RequestBody TwoFactorLoginRequest request) {
        AuthResponse response = authService.validateTwoFactorLogin(request.getEmail(), request.getCode());
        return ResponseEntity.ok(response);
    }

    // 2FA Disable: Kodu doğrulayarak 2FA'yı kapat
    @PostMapping("/2fa/disable")
    @Operation(summary = "Disable 2FA", description = "Disables 2FA after verifying current code")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<String> disableTwoFactor(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TwoFactorVerifyRequest request) {
        authService.disableTwoFactor(userDetails.getUsername(), request.getCode());
        return ResponseEntity.ok("2FA disabled successfully");
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        String remoteAddress = request.getRemoteAddr();
        return remoteAddress == null ? "unknown" : remoteAddress;
    }
}
