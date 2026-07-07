package com.healthsync.auth.service;

import com.healthsync.auth.dto.*;
import com.healthsync.auth.entity.RefreshToken;
import com.healthsync.auth.entity.User;
import com.healthsync.auth.entity.UserStatus;
import com.healthsync.auth.repository.UserRepository;
import com.healthsync.exception.BadRequestException;
import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing user sign-up, login, token refresh rotation, and logout.
 */
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository,
                                 PasswordEncoder passwordEncoder,
                                 JwtService jwtService,
                                 RefreshTokenService refreshTokenService,
                                 AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user, hashes their password, and issues initial access and refresh tokens.
     *
     * @param request the registration request payload
     * @return AuthResponse containing token detail
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BadRequestException("Username is already taken.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("Email is already registered.");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .role(request.role())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        
        String jwtToken = jwtService.generateToken(savedUser);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser.getId());

        return new AuthResponse(
                jwtToken,
                refreshToken.getToken(),
                "Bearer",
                savedUser.getUsername(),
                savedUser.getRole().name()
        );
    }

    /**
     * Authenticates a user by credentials and generates active tokens.
     *
     * @param request the login payload
     * @return AuthResponse containing active tokens
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        
        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                jwtToken,
                refreshToken.getToken(),
                "Bearer",
                user.getUsername(),
                user.getRole().name()
        );
    }

    /**
     * Processes refresh token rotations to generate fresh access/refresh pairs.
     *
     * @param request the refresh request payload
     * @return AuthResponse containing new tokens
     */
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken token = refreshTokenService.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token."));

        User user = token.getUser();
        String jwtToken = jwtService.generateToken(user);
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getId());

        return new AuthResponse(
                jwtToken,
                newRefreshToken.getToken(),
                "Bearer",
                user.getUsername(),
                user.getRole().name()
        );
    }

    /**
     * Invalidates a user's active refresh tokens upon logging out.
     *
     * @param username user logging out
     */
    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        refreshTokenService.deleteByUserId(user.getId());
    }
}
