package com.backend.usermanagement.controller;

import com.backend.usermanagement.dto.request.ChangePasswordRequest;
import com.backend.usermanagement.dto.request.DeactivateAccountRequest;
import com.backend.usermanagement.dto.response.UserResponse;
import com.backend.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User CRUD operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Retrieve all users (Admin only)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by ID (Admin only)")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Soft delete a user by deactivating (Admin only)")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add role to user", description = "Assign a role to a user (Admin only)")
    public ResponseEntity<UserResponse> addRoleToUser(@PathVariable Long id, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.updateUserRole(id, roleName));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change the password for the authenticated user")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                   @AuthenticationPrincipal UserDetails userDetails) {
        userService.changePassword(
                userDetails.getUsername(),
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/deactivate")
    @Operation(summary = "Deactivate account", description = "Deactivate the authenticated user's account")
    public ResponseEntity<String> deactivateAccount(@Valid @RequestBody DeactivateAccountRequest request,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        userService.deactivateAccount(userDetails.getUsername(), request.getPassword());
        return ResponseEntity.ok("Account deactivated successfully");
    }
}

