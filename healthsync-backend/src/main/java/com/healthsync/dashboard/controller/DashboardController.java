package com.healthsync.dashboard.controller;

import com.healthsync.dashboard.dto.*;
import com.healthsync.dashboard.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller exposing endpoints for District Monitoring and consolidations dashboards.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Endpoints for district monitoring and operational dashboards")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    @Operation(summary = "Get overall operational summary metrics")
    public ResponseEntity<DashboardSummaryResponse> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    @GetMapping("/districts")
    @Operation(summary = "Get summary metrics grouped by district")
    public ResponseEntity<List<DistrictSummaryResponse>> getDistricts() {
        return ResponseEntity.ok(dashboardService.getDistricts());
    }

    @GetMapping("/health-centers")
    @Operation(summary = "Get detailed operational statuses of all health centers")
    public ResponseEntity<List<HealthCenterStatusResponse>> getHealthCenters() {
        return ResponseEntity.ok(dashboardService.getHealthCenters());
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get consolidated list of critical alerts across all clinics")
    public ResponseEntity<AlertResponse> getAlerts() {
        return ResponseEntity.ok(dashboardService.getAlerts());
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get 7-day historical trends for resources and footfall")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(dashboardService.getAnalytics());
    }

    @GetMapping("/map")
    @Operation(summary = "Get geographical positions, status flags, and health scores for maps")
    public ResponseEntity<List<MapLocationResponse>> getMapLocations() {
        return ResponseEntity.ok(dashboardService.getMapLocations());
    }
}
