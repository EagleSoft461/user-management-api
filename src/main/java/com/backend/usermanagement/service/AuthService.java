package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.EmailVerificationToken;
import com.backend.usermanagement.domain.entity.PasswordResetToken;
import com.backend.usermanagement.domain.entity.RefreshToken;
import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.dto.response.AuthResponse;
import com.backend.usermanagement.repository.EmailVerificationTokenRepository;
import com.backend.usermanagement.repository.PasswordResetTokenRepository;
import com.backend.usermanagement.repository.UserRepository;
import com.backend.usermanagement.security.JwtUtil;
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
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;

    public AuthService(AuthenticationManager authenticationManager,
                      JwtUtil jwtUtil,
                      UserService userService,
                      PasswordResetTokenRepository passwordResetTokenRepository,
                      EmailVerificationTokenRepository emailVerificationTokenRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      RefreshTokenService refreshTokenService,
                      EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
    }

    @Transactional
    public AuthResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        User user = userService.findByEmail(email);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(email);
        
        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), email, "Login successful");
    }

    @Transactional
    public AuthResponse register(String email, String password) {
        User user = userService.registerUser(email, password);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(email);
        
        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        // Generate email verification token and send email
        sendVerificationEmail(user);

        return new AuthResponse(accessToken, refreshToken.getToken(), email, 
                "Registration successful. Please check your email to verify your account.");
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
        
        // Send email with reset link
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        return token;
    }

    // Verify email with token
    @Transactional
    public void verifyEmail(String token) {
        // Find token in DB
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        // Check if expired
        if (verificationToken.isExpired()) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("Verification token has expired. Please request a new one.");
        }

        // Mark user as verified
        User user = verificationToken.getUser();
        user.verifyEmail();
        userRepository.save(user);

        // Delete used token
        emailVerificationTokenRepository.delete(verificationToken);
    }

    // Resend verification email (if token expired or email not received)
    @Transactional
    public void resendVerificationEmail(String email) {
        User user = userService.findByEmail(email);

        if (user.isEmailVerified()) {
            throw new IllegalStateException("Email is already verified");
        }

        sendVerificationEmail(user);
    }

    // Helper: Generate token and send verification email
    private void sendVerificationEmail(User user) {
        // Delete old token if exists
        emailVerificationTokenRepository.deleteByUser(user);

        // Generate UUID token, valid for 24 hours
        String token = java.util.UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        emailVerificationTokenRepository.save(verificationToken);

        // Send email
        emailService.sendVerificationEmail(user.getEmail(), token);
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
