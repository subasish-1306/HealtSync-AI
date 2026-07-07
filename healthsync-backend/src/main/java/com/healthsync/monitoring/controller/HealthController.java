package com.healthsync.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.Map;

/**
 * REST controller exposing application heartbeat and database connection statuses.
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@Tag(name = "Health Check", description = "Monitor service status and health indicators")
public class HealthController {

    private final DataSource dataSource;

    @GetMapping
    @Operation(summary = "Check database and service health status")
    public ResponseEntity<Map<String, Object>> checkHealth() {
        String dbStatus = "UP";
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isClosed()) {
                dbStatus = "DOWN";
            }
        } catch (Exception e) {
            dbStatus = "DOWN (Error: " + e.getMessage() + ")";
        }

        return ResponseEntity.ok(Map.of(
                "applicationStatus", "UP",
                "databaseStatus", dbStatus,
                "version", "1.0.0-SNAPSHOT",
                "timestamp", Instant.now().toString()
        ));
    }
}
