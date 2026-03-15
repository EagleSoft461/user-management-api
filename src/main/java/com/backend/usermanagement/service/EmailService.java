package com.backend.usermanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    // application.properties'den okur
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.base-url}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        // Doğrulama linki: http://localhost:8081/auth/verify-email?token=UUID
        String verificationLink = baseUrl + "/auth/verify-email?token=" + token;

        String subject = "Verify your email address";
        String body = buildVerificationEmailBody(verificationLink);

        sendEmail(to, subject, body);
    }

    public void sendPasswordResetEmail(String to, String token) {
        String resetLink = baseUrl + "/auth/reset-password?token=" + token;

        String subject = "Reset your password";
        String body = buildPasswordResetEmailBody(resetLink);

        sendEmail(to, subject, body);
    }

    // HTML email gönderir
    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // true = multipart (HTML destekler)
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            // true = HTML içerik
            helper.setText(body, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email to: " + to, e);
        }
    }

    private String buildVerificationEmailBody(String verificationLink) {
        return """
                <html>
                <body>
                    <h2>Verify Your Email Address</h2>
                    <p>Thank you for registering. Please click the link below to verify your email address.</p>
                    <p>This link will expire in <strong>24 hours</strong>.</p>
                    <a href="%s" style="background-color:#4CAF50;color:white;padding:10px 20px;text-decoration:none;border-radius:4px;">
                        Verify Email
                    </a>
                    <p>Or copy this link: %s</p>
                    <p>If you did not create an account, please ignore this email.</p>
                </body>
                </html>
                """.formatted(verificationLink, verificationLink);
    }

    private String buildPasswordResetEmailBody(String resetLink) {
        return """
                <html>
                <body>
                    <h2>Reset Your Password</h2>
                    <p>You requested a password reset. Click the link below to reset your password.</p>
                    <p>This link will expire in <strong>15 minutes</strong>.</p>
                    <a href="%s" style="background-color:#2196F3;color:white;padding:10px 20px;text-decoration:none;border-radius:4px;">
                        Reset Password
                    </a>
                    <p>Or copy this link: %s</p>
                    <p>If you did not request a password reset, please ignore this email.</p>
                </body>
                </html>
                """.formatted(resetLink, resetLink);
    }
}
