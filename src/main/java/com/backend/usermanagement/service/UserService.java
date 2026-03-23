package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.Role;
import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.dto.response.UserResponse;
import com.backend.usermanagement.repository.RoleRepository;
import com.backend.usermanagement.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String email, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user = new User(email, encodedPassword);

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

        user.addRole(userRole);

        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Cacheable("users")  // Cache key: "users" - tüm liste için
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Cacheable(value = "users", key = "#id")  // Cache key: "users::1", "users::2", ...
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToResponse(user);
    }

    @CacheEvict(value = "users", allEntries = true)  // User silinince cache temizle
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        user.deactivate();
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)  // Rol değişince cache temizle
    public UserResponse updateUserRole(Long userId, String roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));

        user.addRole(role);
        userRepository.save(user);

        return convertToResponse(user);
    }

    @CacheEvict(value = "users", allEntries = true)  // Şifre değişince cache temizle
    public void changePassword(String email, String currentPassword, String newPassword, String confirmPassword) {
        // Validate new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New password and confirm password do not match");
        }

        // Validate new password is different from current
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Find user
        User user = findByEmail(email);

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Encode and save new password
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        user.changePassword(encodedNewPassword);
        userRepository.save(user);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.isActive(),
                user.getCreatedAt(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toSet())
        );
    }

    @CacheEvict(value = "users", allEntries = true)  // Hesap deaktive edilince cache temizle
    public void deactivateAccount(String email, String password) {
        // Find user
        User user = findByEmail(email);

        // Check if already deactivated
        if (!user.isActive()) {
            throw new IllegalArgumentException("Account is already deactivated");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        // Deactivate account
        user.deactivate();
        userRepository.save(user);
    }
}
