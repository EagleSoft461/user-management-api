package com.backend.usermanagement.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_tokens")
public class EmailVerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UUID token - emailde link olarak gönderilir
    @Column(nullable = false, unique = true)
    private String token;

    // Hangi kullanıcıya ait
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 24 saat sonra geçersiz olur
    @Column(nullable = false)
    private LocalDateTime expiryDate;

    protected EmailVerificationToken() {}

    public EmailVerificationToken(String token, User user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    // Token süresi dolmuş mu?
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public String getToken() { return token; }
    public User getUser() { return user; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
}
