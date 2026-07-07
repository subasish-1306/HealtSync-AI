package com.healthsync.notification.controller;

import com.healthsync.notification.dto.NotificationResponse;
import com.healthsync.notification.dto.PreferenceRequest;
import com.healthsync.notification.dto.PreferenceResponse;
import com.healthsync.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing endpoints for User alerts and notifications preferences.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Endpoints for managing user alerts and preferences")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all alerts for a user ordered by timestamp")
    public ResponseEntity<List<NotificationResponse>> getNotificationsForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getNotificationsForUser(userId));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(notificationService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a notification")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences/user/{userId}")
    @Operation(summary = "Get notification channel preferences for a user")
    public ResponseEntity<List<PreferenceResponse>> getPreferencesForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getPreferencesForUser(userId));
    }

    @PostMapping("/preferences")
    @Operation(summary = "Configure a channel preference rule")
    public ResponseEntity<PreferenceResponse> updatePreference(@Valid @RequestBody PreferenceRequest request) {
        return ResponseEntity.ok(notificationService.updatePreference(request));
    }
}
