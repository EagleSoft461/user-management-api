package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.Role;
import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.dto.response.PagedResponse;
import com.backend.usermanagement.dto.response.UserResponse;
import com.backend.usermanagement.repository.RoleRepository;
import com.backend.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Role userRole;

    @BeforeEach
    void setUp() {
        userRole = new Role("USER");
    }

    @Test
    void registerUser_Success() {
        String email = "[email]";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.registerUser(email, password);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode(password);
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        String email = "[email]";
        String password = "password123";

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(email, password);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_UserExists_ReturnsUser() {
        String email = "[email]";
        User user = new User(email, "password");

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void findByEmail_UserNotFound_ThrowsException() {
        String email = "[email]";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.findByEmail(email);
        });
    }

    @Test
    void changePassword_Success() {
        String email = "[email]";
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String confirmPassword = "newPassword456";
        String encodedOldPassword = "encodedOldPassword";
        String encodedNewPassword = "encodedNewPassword";

        User user = new User(email, encodedOldPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedOldPassword)).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> {
            userService.changePassword(email, currentPassword, newPassword, confirmPassword);
        });

        verify(passwordEncoder).matches(currentPassword, encodedOldPassword);
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_PasswordsDoNotMatch_ThrowsException() {
        String email = "[email]";
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String confirmPassword = "differentPassword";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(email, currentPassword, newPassword, confirmPassword);
        });

        assertEquals("New password and confirm password do not match", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_NewPasswordSameAsCurrent_ThrowsException() {
        String email = "[email]";
        String currentPassword = "password123";
        String newPassword = "password123";
        String confirmPassword = "password123";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(email, currentPassword, newPassword, confirmPassword);
        });

        assertEquals("New password must be different from current password", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_IncorrectCurrentPassword_ThrowsException() {
        String email = "[email]";
        String currentPassword = "wrongPassword";
        String newPassword = "newPassword456";
        String confirmPassword = "newPassword456";
        String encodedPassword = "encodedPassword";

        User user = new User(email, encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(currentPassword, encodedPassword)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(email, currentPassword, newPassword, confirmPassword);
        });

        assertEquals("Current password is incorrect", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changePassword_UserNotFound_ThrowsException() {
        String email = "[email]";
        String currentPassword = "oldPassword123";
        String newPassword = "newPassword456";
        String confirmPassword = "newPassword456";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            userService.changePassword(email, currentPassword, newPassword, confirmPassword);
        });

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deactivateAccount_Success() {
        String email = "[email]";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        User user = new User(email, encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> {
            userService.deactivateAccount(email, password);
        });

        assertFalse(user.isActive());
        verify(userRepository).save(user);
    }

    @Test
    void deactivateAccount_AlreadyDeactivated_ThrowsException() {
        String email = "[email]";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        User user = new User(email, encodedPassword);
        user.deactivate();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deactivateAccount(email, password);
        });

        assertEquals("Account is already deactivated", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deactivateAccount_IncorrectPassword_ThrowsException() {
        String email = "[email]";
        String password = "wrongPassword";
        String encodedPassword = "encodedPassword";

        User user = new User(email, encodedPassword);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deactivateAccount(email, password);
        });

        assertEquals("Incorrect password", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== Pagination Tests ====================

    @Test
    void getUsersPaged_NoFilter_ReturnsPagedResponse() {
        User user1 = new User("[email1]", "pass1");
        User user2 = new User("[email2]", "pass2");
        List<User> users = List.of(user1, user2);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 2);

        when(userRepository.findAllWithRolesPaged(any(Pageable.class))).thenReturn(page);

        PagedResponse<UserResponse> result = userService.getUsersPaged(0, 10, "id", "asc", null, null, null);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertTrue(result.isFirst());
        assertTrue(result.isLast());
    }

    @Test
    void getUsersPaged_WithActiveFilter_ReturnsFilteredPage() {
        User activeUser = new User("[email]", "pass");
        Page<User> page = new PageImpl<>(List.of(activeUser), PageRequest.of(0, 10), 1);

        when(userRepository.findByActive(eq(true), any(Pageable.class))).thenReturn(page);

        PagedResponse<UserResponse> result = userService.getUsersPaged(0, 10, "id", "asc", true, null, null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByActive(eq(true), any(Pageable.class));
    }

    @Test
    void getUsersPaged_WithEmailFilter_ReturnsFilteredPage() {
        User user = new User("test@example.com", "pass");
        Page<User> page = new PageImpl<>(List.of(user), PageRequest.of(0, 10), 1);

        when(userRepository.findByEmailContainingIgnoreCase(eq("test"), any(Pageable.class))).thenReturn(page);

        PagedResponse<UserResponse> result = userService.getUsersPaged(0, 10, "id", "asc", null, "test", null);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByEmailContainingIgnoreCase(eq("test"), any(Pageable.class));
    }

    @Test
    void getUsersPaged_WithRoleFilter_ReturnsFilteredPage() {
        User adminUser = new User("[email]", "pass");
        Page<User> page = new PageImpl<>(List.of(adminUser), PageRequest.of(0, 10), 1);

        when(userRepository.findByRoleName(eq("ADMIN"), any(Pageable.class))).thenReturn(page);

        PagedResponse<UserResponse> result = userService.getUsersPaged(0, 10, "id", "asc", null, null, "ADMIN");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findByRoleName(eq("ADMIN"), any(Pageable.class));
    }

    @Test
    void getUsersPaged_DescendingSort_CallsRepositoryWithCorrectSort() {
        Page<User> page = new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
        when(userRepository.findAllWithRolesPaged(any(Pageable.class))).thenReturn(page);

        PagedResponse<UserResponse> result = userService.getUsersPaged(0, 10, "createdAt", "desc", null, null, null);

        assertNotNull(result);
        verify(userRepository).findAllWithRolesPaged(any(Pageable.class));
    }
}
