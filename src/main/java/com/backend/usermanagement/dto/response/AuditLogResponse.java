package com.backend.usermanagement.dto.response;

import com.backend.usermanagement.domain.entity.AuditAction;
import com.backend.usermanagement.domain.entity.AuditLog;

import java.time.LocalDateTime;

public class AuditLogResponse {

    private Long id;
    private String userEmail;
    private AuditAction action;
    private boolean success;
    private String ipAddress;
    private String details;
    private LocalDateTime timestamp;

    public AuditLogResponse(AuditLog log) {
        this.id = log.getId();
        this.userEmail = log.getUserEmail();
        this.action = log.getAction();
        this.success = log.isSuccess();
        this.ipAddress = log.getIpAddress();
        this.details = log.getDetails();
        this.timestamp = log.getTimestamp();
    }

    public Long getId() { return id; }
    public String getUserEmail() { return userEmail; }
    public AuditAction getAction() { return action; }
    public boolean isSuccess() { return success; }
    public String getIpAddress() { return ipAddress; }
    public String getDetails() { return details; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
