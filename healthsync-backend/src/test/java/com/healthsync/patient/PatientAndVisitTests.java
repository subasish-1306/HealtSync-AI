package com.healthsync.patient;

import com.healthsync.common.event.PatientAnalyticsEvent;
import com.healthsync.common.event.VisitCompletedEvent;
import com.healthsync.district.entity.District;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.district.entity.HealthCenterType;
import com.healthsync.patient.dto.EmergencyContactDto;
import com.healthsync.patient.dto.PatientRequest;
import com.healthsync.patient.dto.PatientResponse;
import com.healthsync.patient.dto.VisitRequest;
import com.healthsync.patient.dto.VisitResponse;
import com.healthsync.patient.dto.DiseaseCategoryRequest;
import com.healthsync.patient.dto.DiseaseCategoryResponse;
import com.healthsync.patient.service.PatientService;
import com.healthsync.patient.service.VisitService;
import com.healthsync.patient.service.DiseaseCategoryService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Integration test verifying full patient lifecycle workflows and analytics events.
 */
@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
@Transactional
public class PatientAndVisitTests {

    @Autowired
    private PatientService patientService;

    @Autowired
    private VisitService visitService;

    @Autowired
    private DiseaseCategoryService diseaseCategoryService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    public void testCompletePatientAndVisitWorkflow() {
        // Ensure patient_id_seq exists for testing (H2 doesn't execute Flyway by default in tests)
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS patient_id_seq START WITH 1 INCREMENT BY 1").executeUpdate();

        // 1. Setup District & Health Center
        District district = District.builder()
                .name("Test District")
                .region("North")
                .population(500000L)
                .build();
        district.setCreatedBy("test-admin");
        district.setUpdatedBy("test-admin");
        entityManager.persist(district);

        HealthCenter center = HealthCenter.builder()
                .name("Test CHC")
                .type(HealthCenterType.CHC)
                .address("123 Main St")
                .capacity(100)
                .district(district)
                .build();
        center.setCreatedBy("test-admin");
        center.setUpdatedBy("test-admin");
        entityManager.persist(center);
        entityManager.flush();

        // 2. Create Disease Category
        DiseaseCategoryRequest diseaseRequest = new DiseaseCategoryRequest(
                "A09",
                "Gastroenteritis",
                "Waterborne infectious disease",
                2,
                true,
                true
        );
        DiseaseCategoryResponse diseaseResponse = diseaseCategoryService.createCategory(diseaseRequest);
        Assertions.assertNotNull(diseaseResponse.id());
        Assertions.assertEquals("A09", diseaseResponse.code());

        // 3. Register Patient (Age calculated from DOB 1995-05-15)
        EmergencyContactDto contactDto = new EmergencyContactDto(
                "Jane Doe",
                "Spouse",
                "9876543210"
        );
        PatientRequest patientRequest = new PatientRequest(
                null,
                "MRN-2026-001",
                "John Doe",
                "Male",
                LocalDate.of(1995, 5, 15),
                "O+",
                "9998887776",
                "456 Lane Road",
                "ACTIVE",
                district.getId(),
                center.getId(),
                contactDto
        );
        PatientResponse patientResponse = patientService.createPatient(patientRequest);
        Assertions.assertNotNull(patientResponse.id());
        Assertions.assertNotNull(patientResponse.patientId());
        Assertions.assertTrue(patientResponse.patientId().startsWith("PAT-"));
        Assertions.assertEquals(31, patientResponse.age()); // 2026 - 1995 = 31

        // Verify PatientAnalyticsEvent was published
        long patientEventsCount = applicationEvents.stream(PatientAnalyticsEvent.class).count();
        Assertions.assertEquals(1, patientEventsCount);
        PatientAnalyticsEvent patientEvent = applicationEvents.stream(PatientAnalyticsEvent.class).findFirst().orElseThrow();
        Assertions.assertEquals(patientResponse.patientId(), patientEvent.patientId());
        Assertions.assertEquals("ACTIVE", patientEvent.status());

        // 4. Log Patient Visit
        VisitRequest visitRequest = new VisitRequest(
                patientResponse.id(),
                center.getId(),
                null,
                Instant.now(),
                LocalTime.of(10, 30),
                "OPD",
                "FIRST_VISIT",
                "Fever and stomach ache",
                "Infectious diarrhea",
                "RX-12345",
                "COMPLETED",
                25, // visit duration
                15, // waiting time
                diseaseResponse.id()
        );
        VisitResponse visitResponse = visitService.createVisit(visitRequest);
        Assertions.assertNotNull(visitResponse.id());
        Assertions.assertEquals("COMPLETED", visitResponse.status());
        Assertions.assertEquals("ADULT", visitResponse.ageGroup()); // 31 years -> ADULT

        // Verify VisitCompletedEvent was published
        long visitEventsCount = applicationEvents.stream(VisitCompletedEvent.class).count();
        Assertions.assertEquals(1, visitEventsCount);
        VisitCompletedEvent visitEvent = applicationEvents.stream(VisitCompletedEvent.class).findFirst().orElseThrow();
        Assertions.assertEquals(visitResponse.id(), visitEvent.visitId());
        Assertions.assertEquals("OPD", visitEvent.department());
        Assertions.assertEquals("ADULT", visitEvent.ageGroup());
        Assertions.assertEquals("A09", visitEvent.diseaseCategoryCode());
        Assertions.assertTrue(visitEvent.isCommunicable());
    }
}
