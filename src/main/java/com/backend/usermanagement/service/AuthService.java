package com.backend.usermanagement.service;

import com.backend.usermanagement.domain.entity.User;
import com.backend.usermanagement.dto.response.AuthResponse;
import com.backend.usermanagement.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthService(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    public AuthResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = jwtUtil.generateToken(email);

        return new AuthResponse(token, email, "Login successful");
    }

    public AuthResponse register(String email, String password) {
        User user = userService.registerUser(email, password);
        String token = jwtUtil.generateToken(email);

        return new AuthResponse(token, email, "Registration successful");
    }
}
