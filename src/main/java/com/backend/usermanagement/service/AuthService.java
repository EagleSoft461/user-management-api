package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.AuditAction;
import com.backend.usermanagement.domain.entity.EmailVerificationToken;
import com.backend.usermanagement.domain.entity.PasswordResetToken;
import com.backend.usermanagement.domain.entity.RefreshToken;
import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.dto.response.AuthResponse;
import com.backend.usermanagement.dto.response.TwoFactorSetupResponse;
import com.backend.usermanagement.repository.EmailVerificationTokenRepository;
import com.backend.usermanagement.repository.PasswordResetTokenRepository;
import com.backend.usermanagement.repository.UserRepository;
import com.backend.usermanagement.security.JwtUtil;
import dev.samstevens.totp.exceptions.QrGenerationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final AuditLogService auditLogService;
    private final TwoFactorService twoFactorService;
    private final EmailService emailService;

    public AuthService(AuthenticationManager authenticationManager,
                      JwtUtil jwtUtil,
                      UserService userService,
                      EmailVerificationTokenRepository emailVerificationTokenRepository,
                      PasswordResetTokenRepository passwordResetTokenRepository,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      RefreshTokenService refreshTokenService,
                      AuditLogService auditLogService,
                      TwoFactorService twoFactorService,
                      EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
        this.twoFactorService = twoFactorService;
        this.emailService = emailService;
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

        if (!user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is not verified. Please verify your email before logging in.");
        }

        // 2FA aktifse token vermeden önce kod iste
        if (user.isTwoFactorEnabled()) {
            AuthResponse response = new AuthResponse(null, null, null, email, "2FA code required");
            response.setTwoFactorRequired(true);
            return response;
        }

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
        user.markEmailUnverified();
        userRepository.save(user);

        String verificationToken = createEmailVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
        
        // Generate access token
        String accessToken = jwtUtil.generateToken(email);
        
        // Generate refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        AuthResponse response = new AuthResponse(
                user.getId(),
                accessToken,
                refreshToken.getToken(),
                email,
                "Registration successful. Please verify your email."
        );
        return response;
    }

    @Transactional
    public void resendVerificationToken(String email) {
        User user = userService.findByEmail(email);
        if (user.isEmailVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        String verificationToken = createEmailVerificationToken(user);
        emailService.sendVerificationEmail(user.getEmail(), verificationToken);
    }

    @Transactional
    public void verifyEmail(String token) {
        EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid verification token"));

        if (verificationToken.isExpired()) {
            emailVerificationTokenRepository.delete(verificationToken);
            throw new IllegalArgumentException("Verification token has expired");
        }

        User user = verificationToken.getUser();
        user.markEmailVerified();
        userRepository.save(user);
        emailVerificationTokenRepository.delete(verificationToken);
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
        emailService.sendPasswordResetEmail(user.getEmail(), token);
        
        return token;
    }

    private String createEmailVerificationToken(User user) {
        emailVerificationTokenRepository.deleteByUser(user);
        String token = java.util.UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
        emailVerificationTokenRepository.save(verificationToken);
        return token;
    }
    
    // 2FA Setup: Secret üret ve QR code döndür
    @Transactional
    public TwoFactorSetupResponse setupTwoFactor(String email) throws QrGenerationException {
        User user = userService.findByEmail(email);
        String secret = twoFactorService.generateSecret();
        user.enableTwoFactor(secret);
        userRepository.save(user);
        String qrCodeUri = twoFactorService.generateQrCodeDataUri(email, secret);
        return new TwoFactorSetupResponse(secret, qrCodeUri);
    }

    // 2FA Verify: Kodu doğrula ve 2FA'yı aktif et
    @Transactional
    public void verifyTwoFactor(String email, String code) {
        User user = userService.findByEmail(email);
        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), code)) {
            throw new IllegalArgumentException("Invalid 2FA code");
        }
    }

    // 2FA Login: Email/şifre doğrulandıktan sonra kod ile token al
    @Transactional
    public AuthResponse validateTwoFactorLogin(String email, String code) {
        User user = userService.findByEmail(email);
        if (!user.isTwoFactorEnabled()) {
            throw new IllegalArgumentException("2FA is not enabled for this user");
        }
        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), code)) {
            auditLogService.log(email, AuditAction.LOGIN, false, "unknown", "2FA code invalid");
            throw new IllegalArgumentException("Invalid 2FA code");
        }
        String accessToken = jwtUtil.generateToken(email);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        auditLogService.log(email, AuditAction.LOGIN, true, "unknown", "Login with 2FA successful");
        return new AuthResponse(user.getId(), accessToken, refreshToken.getToken(), email, "Login successful");
    }

    // 2FA Disable
    @Transactional
    public void disableTwoFactor(String email, String code) {
        User user = userService.findByEmail(email);
        if (!twoFactorService.verifyCode(user.getTwoFactorSecret(), code)) {
            throw new IllegalArgumentException("Invalid 2FA code");
        }
        user.disableTwoFactor();
        userRepository.save(user);
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
