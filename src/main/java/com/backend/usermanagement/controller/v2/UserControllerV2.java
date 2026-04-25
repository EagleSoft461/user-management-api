package com.backend.usermanagement.controller.v2;

import com.backend.usermanagement.dto.request.ChangePasswordRequest;
import com.backend.usermanagement.dto.request.DeactivateAccountRequest;
import com.backend.usermanagement.dto.request.UpdateProfileRequest;
import com.backend.usermanagement.dto.response.PagedResponse;
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

@RestController
@RequestMapping("/api/v2/users")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management V2", description = "User management endpoints - Version 2 (with pagination & filtering)")
public class UserControllerV2 {

    private final UserService userService;

    public UserControllerV2(UserService userService) {
        this.userService = userService;
    }

    // V2: Pagination + Filtering (v1'deki basit liste yerine)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V2] Get users with pagination & filtering",
               description = "Returns paginated users with optional filtering by active, email, role")
    public ResponseEntity<PagedResponse<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String role) {
        return ResponseEntity.ok(userService.getUsersPaged(page, size, sortBy, sortDir, active, email, role));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V2] Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V2] Deactivate user")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/{id}/roles/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[V2] Add role to user")
    public ResponseEntity<UserResponse> addRoleToUser(@PathVariable Long id, @PathVariable String roleName) {
        return ResponseEntity.ok(userService.updateUserRole(id, roleName));
    }

    @PostMapping("/change-password")
    @Operation(summary = "[V2] Change password")
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
    @Operation(summary = "[V2] Deactivate own account")
    public ResponseEntity<String> deactivateAccount(@Valid @RequestBody DeactivateAccountRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        userService.deactivateAccount(userDetails.getUsername(), request.getPassword());
        return ResponseEntity.ok("Account deactivated successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "[V2] Get own profile")
    public ResponseEntity<ProfileResponse> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getMyProfile(userDetails.getUsername()));
    }

    @PutMapping("/me")
    @Operation(summary = "[V2] Update own profile")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateMyProfile(userDetails.getUsername(), request));
    }
}
