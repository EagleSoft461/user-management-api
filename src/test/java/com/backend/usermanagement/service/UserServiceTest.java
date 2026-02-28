package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.Role;
import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.repository.RoleRepository;
import com.backend.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
}
