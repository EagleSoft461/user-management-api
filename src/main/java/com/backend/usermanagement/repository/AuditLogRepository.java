package com.backend.usermanagement.repository;

import com.backend.usermanagement.domain.entity.AuditAction;
import com.backend.usermanagement.domain.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Belirli bir kullanıcının tüm loglarını getir
    List<AuditLog> findByUserEmailOrderByTimestampDesc(String userEmail);

    // Belirli bir action türündeki logları getir
    List<AuditLog> findByActionOrderByTimestampDesc(AuditAction action);

    // Sadece başarısız işlemleri getir (güvenlik analizi için)
    List<AuditLog> findBySuccessFalseOrderByTimestampDesc();
}
