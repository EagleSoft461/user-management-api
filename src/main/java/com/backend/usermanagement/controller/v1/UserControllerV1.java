package com.backend.usermanagement.controller.v1;

import com.backend.usermanagement.dto.request.ChangePasswordRequest;
import com.backend.usermanagement.dto.request.DeactivateAccountRequest;
import com.backend.usermanagement.dto.request.UpdateProfileRequest;
import com.backend.usermanagement.dto.response.ProfileResponse;
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
@RequestMapping("/api/v1/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management V1", description = "User management endpoints - Version 1")
public class UserControllerV1 {

    private final UserService userService;

    public UserControllerV1(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V1] Get all users", description = "Returns all users as a simple list")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V1] Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V1] Deactivate user")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V1] Add role to user")
    public ResponseEntity<UserResponse> addRoleToUser(@PathVariable Long id, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.updateUserRole(id, roleName));
    }

    @PostMapping("/change-password")
    @Operation(summary = "[V1] Change password")
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
    @Operation(summary = "[V1] Deactivate own account")
    public ResponseEntity<String> deactivateAccount(@Valid @RequestBody DeactivateAccountRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        userService.deactivateAccount(userDetails.getUsername(), request.getPassword());
        return ResponseEntity.ok("Account deactivated successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "[V1] Get own profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/me")
    @Operation(summary = "[V1] Update own profile")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateMyProfile(userDetails.getUsername(), request));
    }
}
