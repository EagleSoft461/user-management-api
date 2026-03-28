package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.AuditAction;
import com.backend.usermanagement.domain.entity.AuditLog;
import com.backend.usermanagement.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    // Ayrı transaction'da çalışır — parent rollback olsa bile log kaydedilir
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String userEmail, AuditAction action, boolean success, String ipAddress, String details) {
        AuditLog auditLog = new AuditLog(userEmail, action, success, ipAddress, details);
        auditLogRepository.save(auditLog);
    }

    // Tüm logları getir (admin)
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    // Belirli kullanıcının logları
    public List<AuditLog> getLogsByUser(String email) {
        return auditLogRepository.findByUserEmailOrderByTimestampDesc(email);
    }

    // Sadece başarısız işlemler
    public List<AuditLog> getFailedLogs() {
        return auditLogRepository.findBySuccessFalseOrderByTimestampDesc();
    }
}
