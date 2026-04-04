package com.backend.usermanagement.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi kullanıcı bu işlemi yaptı
    @Column(nullable = false)
    private String userEmail;

    // Ne tür bir işlem yapıldı (LOGIN, REGISTER, vb.)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuditAction action;

    // İşlem başarılı mıydı?
    @Column(nullable = false)
    private boolean success;

    // Kullanıcının IP adresi
    private String ipAddress;

    // Ek bilgi (hata mesajı, değiştirilen rol, vb.)
    private String details;

    // İşlem zamanı
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public AuditLog() {}

    public AuditLog(String userEmail, AuditAction action, boolean success, String ipAddress, String details) {
        this.userEmail = userEmail;
        this.action = action;
        this.success = success;
        this.ipAddress = ipAddress;
        this.details = details;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public AuditAction getAction() { return action; }
    public boolean isSuccess() { return success; }
    public String getIpAddress() { return ipAddress; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
