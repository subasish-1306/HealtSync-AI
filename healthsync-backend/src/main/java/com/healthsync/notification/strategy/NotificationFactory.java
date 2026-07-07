package com.healthsync.notification.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory class resolving active NotificationStrategy implementations based on user preference channels.
 */
@Component
@RequiredArgsConstructor
public class NotificationFactory {

    private final List<NotificationStrategy> strategies;

    public List<NotificationStrategy> getStrategiesForChannels(List<String> channels) {
        return strategies.stream()
                .filter(strategy -> channels.stream().anyMatch(strategy::supportsChannel))
                .collect(Collectors.toList());
    }
}
