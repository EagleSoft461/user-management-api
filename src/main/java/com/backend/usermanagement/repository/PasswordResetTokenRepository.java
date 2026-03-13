package com.backend.usermanagement.repository;

import com.backend.usermanagement.domain.entity.PasswordResetToken;
import com.backend.usermanagement.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    
    Optional<PasswordResetToken> findByToken(String token);
    
    void deleteByUser(User user);
    
    void deleteByExpiryDateBefore(LocalDateTime now);
}
