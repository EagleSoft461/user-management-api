package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.AuditAction;
import com.backend.usermanagement.domain.entity.PasswordResetToken;
import com.backend.usermanagement.domain.entity.RefreshToken;
import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.dto.response.AuthResponse;
import com.backend.usermanagement.repository.PasswordResetTokenRepository;
import com.backend.usermanagement.repository.UserRepository;
import com.backend.usermanagement.security.JwtUtil;
import com.backend.usermanagement.service.AuditLogService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;

    public AuthService(AuthenticationManager authenticationManager,
                      JwtUtil jwtUtil,
                      UserService userService,
                      PasswordResetTokenRepository passwordResetTokenRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      RefreshTokenService refreshTokenService,
                      AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public AuthResponse login(String email, String password) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (Exception e) {
            auditLogService.log(email, AuditAction.LOGIN,
                    false, "unknown", "Login failed: " + e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = userService.findByEmail(email);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(email);
        
        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        auditLogService.log(email, AuditAction.LOGIN, true, "unknown", "Login successful");

        return new AuthResponse(user.getId(), accessToken, refreshToken.getToken(), email, "Login successful");
    }

    @Transactional
    public AuthResponse register(String email, String password) {
        User user = userService.registerUser(email, password);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(email);
        
        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(user.getId(), accessToken, refreshToken.getToken(), email, "Registration successful");
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenString) {
        // Verify refresh token
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenString);
        
        User user = refreshToken.getUser();
        
        // Generate new access token
        String newAccessToken = jwtUtil.generateToken(user.getEmail());
        
        // Token rotation: Create new refresh token and revoke old one
        refreshTokenService.revokeRefreshToken(refreshTokenString);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(
                newAccessToken, 
                newRefreshToken.getToken(), 
                user.getEmail(), 
                "Token refreshed successfully"
        );
    }

    @Transactional
    public String createPasswordResetToken(String email) {
        User user = userService.findByEmail(email);
        
        // Delete any existing tokens for this user
        passwordResetTokenRepository.deleteByUser(user);
        
        // Generate new token (UUID)
        String token = java.util.UUID.randomUUID().toString();
        
        // Set expiry to 15 minutes from now
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(15);
        
        // Save token
        PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
        passwordResetTokenRepository.save(resetToken);
        
        // In production, send email here
        // emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        return token;
    }
    
    @Transactional
    public void resetPassword(String token, String newPassword, String confirmPassword) {
        // Validate passwords match
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        
        // Find token
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
        
        // Check if expired
        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token has expired");
        }
        
        // Get user and update password
        User user = resetToken.getUser();
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedPassword);
        userRepository.save(user);
        
        // Revoke all refresh tokens (security: force re-login after password change)
        refreshTokenService.revokeAllUserTokens(user);
        
        // Delete used token
        passwordResetTokenRepository.delete(resetToken);
    }
}
