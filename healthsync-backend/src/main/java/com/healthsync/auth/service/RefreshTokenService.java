package com.healthsync.auth.service;

import com.healthsync.auth.entity.RefreshToken;
import com.healthsync.auth.entity.User;
import com.healthsync.auth.repository.RefreshTokenRepository;
import com.healthsync.auth.repository.UserRepository;
import com.healthsync.exception.BadRequestException;
import com.healthsync.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service managing user session refresh tokens.
 */
@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshTokenDurationMs;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            @Value("${security.jwt.refresh-token-expiration-ms:604800000}") long refreshTokenDurationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.refreshTokenDurationMs = refreshTokenDurationMs;
    }

    /**
     * Looks up a refresh token object.
     *
     * @param token the token string
     * @return the RefreshToken object if found
     */
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Generates a new refresh token for a user, automatically revoking old ones.
     *
     * @param userId the user's UUID
     * @return the generated RefreshToken
     */
    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Revoke any existing active refresh tokens to enforce single active session per user
        refreshTokenRepository.deleteByUser(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Confirms the token is not expired or revoked.
     *
     * @param token the RefreshToken object
     * @return the validated token
     */
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.isExpired()) {
            refreshTokenRepository.delete(token);
            throw new BadRequestException("Refresh token has expired. Please sign in again.");
        }
        if (token.isRevoked()) {
            throw new BadRequestException("Refresh token has been revoked.");
        }
        return token;
    }

    /**
     * Clears all refresh tokens for a user.
     *
     * @param userId the user's UUID
     */
    @Transactional
    public void deleteByUserId(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        refreshTokenRepository.deleteByUser(user);
    }
}
