package com.healthsync.auth.service;

import com.healthsync.auth.dto.ChangePasswordRequest;
import com.healthsync.auth.dto.UserResponse;
import com.healthsync.auth.entity.User;
import com.healthsync.auth.repository.UserRepository;
import com.healthsync.exception.BadRequestException;
import com.healthsync.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service handling profile lookups and user modifications like password changes.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Retrieves user profile details by username.
     *
     * @param username user login identifier
     * @return response details containing profile attributes
     */
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        return mapToUserResponse(user);
    }

    /**
     * Changes user password after validating current credential matching.
     *
     * @param request  the change password payload
     * @param username active username identifier
     * @return updated user details
     */
    @Transactional
    public UserResponse changePassword(ChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Current password does not match.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                user.getStatus().name()
        );
    }
}
