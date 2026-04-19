package com.backend.usermanagement.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:no-reply@example.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String email, String verificationToken) {
        String verificationLink = baseUrl + "/auth/verify-email?token=" + verificationToken;
        String subject = "Verify your email address";
        String body = "Welcome!\n\nPlease verify your email by clicking the link below:\n"
                + verificationLink
                + "\n\nIf you did not create an account, you can ignore this email.";

        sendEmail(email, subject, body);
    }

    public void sendPasswordResetEmail(String email, String resetToken) {
        String resetLink = baseUrl + "/auth/reset-password?token=" + resetToken;
        String subject = "Password reset request";
        String body = "We received a password reset request.\n\nUse this token or link to reset your password:\n"
                + "Token: " + resetToken + "\n"
                + "Link: " + resetLink
                + "\n\nIf this was not you, ignore this email.";

        sendEmail(email, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
