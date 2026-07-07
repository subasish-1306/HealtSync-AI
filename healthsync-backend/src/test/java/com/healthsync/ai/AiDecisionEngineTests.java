package com.healthsync.ai;

import com.healthsync.ai.dto.HealthScoreResponse;
import com.healthsync.ai.dto.PredictionSummary;
import com.healthsync.ai.service.AiInsightService;
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
import java.util.UUID;

/**
 * Integration test suite validating AI calculations, prediction models, and critical alert triggers.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AiDecisionEngineTests {

    @Autowired
    private AiInsightService aiInsightService;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCompleteDecisionEngineCalculations() {
        // Ensure patient sequence exists for H2
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS patient_id_seq START WITH 1 INCREMENT BY 1").executeUpdate();

        // 1. Setup District & Health Center
        District district = District.builder()
                .name("AI Test District")
                .region("North")
                .population(100000L)
                .build();
        district.setCreatedBy("admin");
        district.setUpdatedBy("admin");
        entityManager.persist(district);

        HealthCenter center = HealthCenter.builder()
                .name("AI Test CHC")
                .type(HealthCenterType.CHC)
                .address("456 Road")
                .capacity(100)
                .district(district)
                .build();
        center.setCreatedBy("admin");
        center.setUpdatedBy("admin");
        entityManager.persist(center);

        // 2. Setup Ward & Bed
        Ward ward = Ward.builder()
                .code("W-AI")
                .name("AI Ward")
                .department("General")
                .floor(1)
                .capacity(10)
                .status("ACTIVE")
                .healthCenter(center)
                .build();
        ward.setCreatedBy("admin");
        ward.setUpdatedBy("admin");
        entityManager.persist(ward);

        Bed bed = Bed.builder()
                .bedNumber("B-AI-01")
                .bedType("GENERAL")
                .availabilityStatus("AVAILABLE")
                .cleaningStatus("CLEAN")
                .maintenanceStatus("FUNCTIONAL")
                .ward(ward)
                .build();
        bed.setCreatedBy("admin");
        bed.setUpdatedBy("admin");
        entityManager.persist(bed);

        // 3. Setup Medicine & Inventory
        MedicineCategory medCat = MedicineCategory.builder()
                .name("Analgesics")
                .description("Pain relievers")
                .build();
        medCat.setCreatedBy("admin");
        medCat.setUpdatedBy("admin");
        entityManager.persist(medCat);

        Medicine medicine = Medicine.builder()
                .name("Paracetamol")
                .genericName("Acetaminophen")
                .code("MED-PARA-500")
                .category(medCat)
                .strength("500mg")
                .unit("Tablet")
                .build();
        medicine.setCreatedBy("admin");
        medicine.setUpdatedBy("admin");
        entityManager.persist(medicine);

        // Setup inventory with quantity less than reorderLevel (triggers shortage alerts/recommendations)
        Inventory inventory = Inventory.builder()
                .healthCenter(center)
                .medicine(medicine)
                .quantity(10)
                .reorderLevel(50)
                .batchNumber("B-PARA-001")
                .expiryDate(Instant.now().plusSeconds(86400 * 30))
                .build();
        inventory.setCreatedBy("admin");
        inventory.setUpdatedBy("admin");
        entityManager.persist(inventory);

        entityManager.flush();

        // 4. Run AI calculations
        UUID centerId = center.getId();

        // Health Score
        HealthScoreResponse healthScore = aiInsightService.getHealthScore(centerId);
        Assertions.assertNotNull(healthScore);
        Assertions.assertEquals(centerId, healthScore.healthCenterId());
        Assertions.assertTrue(healthScore.score() >= 0 && healthScore.score() <= 100);

        // Predictions
        PredictionSummary predictions = aiInsightService.getPredictions(centerId);
        Assertions.assertNotNull(predictions);
        Assertions.assertFalse(predictions.patientLoad().isEmpty());
        Assertions.assertFalse(predictions.bedOccupancy().isEmpty());

        // Alerts & Recommendations
        var alerts = aiInsightService.getAlerts(centerId);
        Assertions.assertNotNull(alerts);
        boolean hasShortageAlert = alerts.stream().anyMatch(a -> "MEDICINE_SHORTAGE".equals(a.alertType()));
        Assertions.assertTrue(hasShortageAlert);

        var recommendations = aiInsightService.getRecommendations(centerId);
        Assertions.assertNotNull(recommendations);
        boolean hasMedicineRec = recommendations.stream().anyMatch(r -> "MEDICINE".equals(r.category()));
        Assertions.assertTrue(hasMedicineRec);
    }
}
