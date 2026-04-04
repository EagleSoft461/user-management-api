package com.backend.usermanagement.controller;

import com.backend.usermanagement.dto.response.AuditLogResponse;
import com.backend.usermanagement.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    // Tüm audit logları - sadece ADMIN
    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {
        List<AuditLogResponse> logs = auditLogService.getAllLogs()
                .stream()
                .map(AuditLogResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    // Belirli kullanıcının logları
    @GetMapping("/user/{email}")
    public ResponseEntity<List<AuditLogResponse>> getLogsByUser(@PathVariable String email) {
        List<AuditLogResponse> logs = auditLogService.getLogsByUser(email)
                .stream()
                .map(AuditLogResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }

    // Sadece başarısız işlemler (güvenlik analizi)
    @GetMapping("/failed")
    public ResponseEntity<List<AuditLogResponse>> getFailedLogs() {
        List<AuditLogResponse> logs = auditLogService.getFailedLogs()
                .stream()
                .map(AuditLogResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }
}
