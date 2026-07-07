package com.healthsync.dashboard;

import com.healthsync.dashboard.dto.*;
import com.healthsync.dashboard.service.DashboardService;
import com.healthsync.bed.entity.Bed;
import com.healthsync.bed.entity.Ward;
import com.healthsync.district.entity.District;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.district.entity.HealthCenterType;
import com.healthsync.inventory.entity.Inventory;
import com.healthsync.inventory.entity.Medicine;
import com.healthsync.inventory.entity.MedicineCategory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Integration test suite validating operational summary aggregations, district queries, and trend metrics.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DashboardTests {

    @Autowired
    private DashboardService dashboardService;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCompleteDashboardAggregation() {
        // Ensure patient sequence exists for H2
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS patient_id_seq START WITH 1 INCREMENT BY 1").executeUpdate();

        // 1. Setup District & Health Center
        District district = District.builder()
                .name("Dashboard Test District")
                .region("East")
                .population(150000L)
                .build();
        district.setCreatedBy("admin");
        district.setUpdatedBy("admin");
        entityManager.persist(district);

        HealthCenter center = HealthCenter.builder()
                .name("Dashboard CHC")
                .type(HealthCenterType.CHC)
                .address("555 Boulevard")
                .capacity(80)
                .district(district)
                .build();
        center.setCreatedBy("admin");
        center.setUpdatedBy("admin");
        entityManager.persist(center);

        // 2. Setup Ward & Bed
        Ward ward = Ward.builder()
                .code("W-DASH")
                .name("Dashboard Ward")
                .department("Pediatrics")
                .floor(1)
                .capacity(5)
                .status("ACTIVE")
                .healthCenter(center)
                .build();
        ward.setCreatedBy("admin");
        ward.setUpdatedBy("admin");
        entityManager.persist(ward);

        Bed bed = Bed.builder()
                .bedNumber("B-DASH-01")
                .bedType("GENERAL")
                .availabilityStatus("AVAILABLE")
                .cleaningStatus("CLEAN")
                .maintenanceStatus("FUNCTIONAL")
                .ward(ward)
                .build();
        bed.setCreatedBy("admin");
        bed.setUpdatedBy("admin");
        entityManager.persist(bed);

        // 3. Setup Medicine & Inventory (under safety margin)
        MedicineCategory medCat = MedicineCategory.builder()
                .name("Antibiotics")
                .description("Bacterial fight")
                .build();
        medCat.setCreatedBy("admin");
        medCat.setUpdatedBy("admin");
        entityManager.persist(medCat);

        Medicine medicine = Medicine.builder()
                .name("Amoxicillin")
                .genericName("Amoxicillin")
                .code("MED-AMOX-500")
                .category(medCat)
                .strength("500mg")
                .unit("Capsule")
                .build();
        medicine.setCreatedBy("admin");
        medicine.setUpdatedBy("admin");
        entityManager.persist(medicine);

        Inventory inventory = Inventory.builder()
                .healthCenter(center)
                .medicine(medicine)
                .quantity(5)
                .reorderLevel(20)
                .batchNumber("B-AMOX-01")
                .expiryDate(Instant.now().plusSeconds(86400 * 30))
                .build();
        inventory.setCreatedBy("admin");
        inventory.setUpdatedBy("admin");
        entityManager.persist(inventory);

        entityManager.flush();

        // 4. Verify Dashboard Summary API Aggregation
        DashboardSummaryResponse summary = dashboardService.getSummary();
        Assertions.assertNotNull(summary);
        Assertions.assertTrue(summary.totalHealthCenters() >= 1);
        Assertions.assertEquals(1, summary.medicineLowStockCount());

        // 5. Verify District Summaries
        List<DistrictSummaryResponse> districtSummaries = dashboardService.getDistricts();
        Assertions.assertFalse(districtSummaries.isEmpty());
        boolean hasTestDistrict = districtSummaries.stream().anyMatch(d -> "Dashboard Test District".equals(d.districtName()));
        Assertions.assertTrue(hasTestDistrict);

        // 6. Verify Health Center status metrics
        List<HealthCenterStatusResponse> hcStatuses = dashboardService.getHealthCenters();
        Assertions.assertFalse(hcStatuses.isEmpty());
        boolean hasTestCenter = hcStatuses.stream().anyMatch(h -> "Dashboard CHC".equals(h.name()));
        Assertions.assertTrue(hasTestCenter);

        // 7. Verify Alerts consolidation
        AlertResponse alerts = dashboardService.getAlerts();
        Assertions.assertNotNull(alerts);
        Assertions.assertFalse(alerts.medicineShortageAlerts().isEmpty());

        // 8. Verify Analytics historical trends
        AnalyticsResponse analytics = dashboardService.getAnalytics();
        Assertions.assertNotNull(analytics);
        Assertions.assertEquals(7, analytics.patientFootfallTrend().size());

        // 9. Verify Maps coordinates
        List<MapLocationResponse> mapLocations = dashboardService.getMapLocations();
        Assertions.assertFalse(mapLocations.isEmpty());
        MapLocationResponse mapLoc = mapLocations.stream()
                .filter(m -> center.getId().equals(m.healthCenterId()))
                .findFirst()
                .orElse(null);
        Assertions.assertNotNull(mapLoc);
        Assertions.assertTrue(mapLoc.latitude() > 0);
        Assertions.assertTrue(mapLoc.longitude() > 0);
    }
}
