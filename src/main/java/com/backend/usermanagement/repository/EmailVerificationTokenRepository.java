package com.backend.usermanagement.repository;

import com.backend.usermanagement.domain.entity.EmailVerificationToken;
import com.backend.usermanagement.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    // Token string'i ile token'ı bul (doğrulama sırasında kullanılır)
    Optional<EmailVerificationToken> findByToken(String token);

    // Kullanıcıya ait token'ı sil (yeni token üretmeden önce eskisini temizle)
    void deleteByUser(User user);
}
