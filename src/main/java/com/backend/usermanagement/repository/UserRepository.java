package com.backend.usermanagement.repository;

import com.backend.usermanagement.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Aktif/pasif filtreleme ile pagination
    Page<User> findByActive(boolean active, Pageable pageable);

    // Email'e göre arama (contains = LIKE %email%)
    Page<User> findByEmailContainingIgnoreCase(String email, Pageable pageable);

    // Role'e göre filtreleme
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    Page<User> findByRoleName(@Param("roleName") String roleName, Pageable pageable);

    // Kombine filtre: active + email arama
    Page<User> findByActiveAndEmailContainingIgnoreCase(boolean active, String email, Pageable pageable);

    // Pagination için roles'ı eager fetch et
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles")
    List<User> findAllWithRoles(Pageable pageable);

    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
           countQuery = "SELECT COUNT(DISTINCT u) FROM User u")
    Page<User> findAllWithRolesPaged(Pageable pageable);
}