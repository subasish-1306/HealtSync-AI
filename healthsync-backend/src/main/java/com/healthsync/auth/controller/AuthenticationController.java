package com.healthsync.auth.controller;

import com.healthsync.auth.dto.*;
import com.healthsync.auth.service.AuthenticationService;
import com.healthsync.auth.service.UserService;
import com.healthsync.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class for authentication operations including signup, signin, refresh, and logout.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    /**
     * Public sign-up endpoint registering users in the system database.
     *
     * @param request register form details
     * @return AuthResponse carrying initial active tokens
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authenticationService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User registered successfully"));
    }

    /**
     * Public login endpoint returning active tokens on successful authentication.
     *
     * @param request login credential form
     * @return AuthResponse with generated tokens
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authenticationService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "User logged in successfully"));
    }

    /**
     * Token refresh endpoint implementing rotation to retrieve clean access/refresh pairs.
     *
     * @param request refresh token payload
     * @return AuthResponse with new tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authenticationService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Tokens refreshed successfully"));
    }

    /**
     * Logout endpoint invalidating the current user session.
     *
     * @param userDetails authenticated security user details
     * @return empty success response wrapper
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authenticationService.logout(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(null, "User logged out successfully"));
    }

    /**
     * Profile fetch endpoint retrieving the currently authenticated user details.
     *
     * @param userDetails authenticated security user details
     * @return user profile response details
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getUserByUsername(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "User profile fetched successfully"));
    }

    /**
     * Authenticated endpoint mapping credential updates.
     *
     * @param request     change password payload
     * @param userDetails authenticated security user details
     * @return updated user details
     */
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<UserResponse>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.changePassword(request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Password updated successfully"));
    }
}
