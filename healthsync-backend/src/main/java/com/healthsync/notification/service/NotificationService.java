package com.healthsync.notification.service;

import com.healthsync.auth.entity.User;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.notification.dto.NotificationResponse;
import com.healthsync.notification.dto.PreferenceRequest;
import com.healthsync.notification.dto.PreferenceResponse;
import com.healthsync.notification.entity.Notification;
import com.healthsync.notification.entity.NotificationPreference;
import com.healthsync.notification.repository.NotificationPreferenceRepository;
import com.healthsync.notification.repository.NotificationRepository;
import com.healthsync.notification.strategy.NotificationFactory;
import com.healthsync.notification.strategy.NotificationStrategy;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for System Notifications.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationFactory notificationFactory;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedDateDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with ID: " + notificationId));
        notification.setStatus("READ");
        return mapToResponse(notificationRepository.save(notification));
    }

    @Transactional
    public void deleteNotification(UUID notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found with ID: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }

    @Transactional(readOnly = true)
    public List<PreferenceResponse> getPreferencesForUser(UUID userId) {
        return preferenceRepository.findByUserId(userId).stream()
                .map(this::mapToPreferenceResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PreferenceResponse updatePreference(PreferenceRequest request) {
        User user = entityManager.find(User.class, request.userId());
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + request.userId());
        }

        NotificationPreference preference = preferenceRepository
                .findByUserIdAndNotificationTypeAndChannel(request.userId(), request.notificationType(), request.channel())
                .orElseGet(() -> NotificationPreference.builder()
                        .user(user)
                        .notificationType(request.notificationType())
                        .channel(request.channel())
                        .build());

        preference.setEnabled(request.enabled());
        return mapToPreferenceResponse(preferenceRepository.save(preference));
    }

    @Transactional
    public NotificationResponse triggerNotification(UUID userId, UUID healthCenterId, String message, String type) {
        User user = userId != null ? entityManager.find(User.class, userId) : null;
        HealthCenter center = healthCenterId != null ? entityManager.find(HealthCenter.class, healthCenterId) : null;

        Notification notification = Notification.builder()
                .message(message)
                .type(type)
                .status("UNREAD")
                .user(user)
                .healthCenter(center)
                .build();

        Notification saved = notificationRepository.save(notification);

        List<String> activeChannels = new ArrayList<>();
        if (userId != null) {
            List<NotificationPreference> preferences = preferenceRepository.findByUserId(userId);
            for (NotificationPreference pref : preferences) {
                if (pref.getNotificationType().equalsIgnoreCase(type) && pref.getEnabled()) {
                    activeChannels.add(pref.getChannel());
                }
            }
        }

        if (activeChannels.isEmpty()) {
            activeChannels.add("IN_APP");
        }

        List<NotificationStrategy> strategies = notificationFactory.getStrategiesForChannels(activeChannels);
        for (NotificationStrategy strategy : strategies) {
            strategy.sendNotification(saved);
        }

        return mapToResponse(saved);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.getStatus(),
                notification.getHealthCenter() != null ? notification.getHealthCenter().getId() : null,
                notification.getHealthCenter() != null ? notification.getHealthCenter().getName() : null,
                notification.getUser() != null ? notification.getUser().getId() : null,
                notification.getUser() != null ? notification.getUser().getUsername() : null,
                notification.getCreatedDate()
        );
    }

    private PreferenceResponse mapToPreferenceResponse(NotificationPreference preference) {
        return new PreferenceResponse(
                preference.getId(),
                preference.getUser().getId(),
                preference.getNotificationType(),
                preference.getChannel(),
                preference.getEnabled()
        );
    }
}
