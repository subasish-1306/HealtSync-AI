package com.healthsync.bed;

import com.healthsync.bed.dto.*;
import com.healthsync.bed.entity.Bed;
import com.healthsync.bed.entity.BedAllocation;
import com.healthsync.bed.service.AllocationService;
import com.healthsync.bed.service.BedService;
import com.healthsync.bed.service.TransferService;
import com.healthsync.bed.service.WardService;
import com.healthsync.common.event.BedAllocatedEvent;
import com.healthsync.common.event.BedReleasedEvent;
import com.healthsync.common.event.WardCapacityReachedEvent;
import com.healthsync.district.entity.District;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.district.entity.HealthCenterType;
import com.healthsync.patient.entity.Patient;
import com.healthsync.patient.entity.PatientEmergencyContact;
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

/**
 * Integration test suite validating Ward capacity limits, Bed occupancy transitions, and Transfer history logging.
 */
@SpringBootTest
@ActiveProfiles("test")
@RecordApplicationEvents
@Transactional
public class BedManagementTests {

    @Autowired
    private WardService wardService;

    @Autowired
    private BedService bedService;

    @Autowired
    private AllocationService allocationService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    public void testCompleteBedManagementLifecycle() {
        // Ensure patient sequence exists for H2
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS patient_id_seq START WITH 1 INCREMENT BY 1").executeUpdate();

        // 1. Setup District, Center & Patients
        District district = District.builder()
                .name("Bed Test District")
                .region("South")
                .population(300000L)
                .build();
        district.setCreatedBy("admin");
        district.setUpdatedBy("admin");
        entityManager.persist(district);

        HealthCenter center = HealthCenter.builder()
                .name("Bed Test Clinic")
                .type(HealthCenterType.PHC)
                .address("789 Lane")
                .capacity(50)
                .district(district)
                .build();
        center.setCreatedBy("admin");
        center.setUpdatedBy("admin");
        entityManager.persist(center);

        PatientEmergencyContact contact1 = PatientEmergencyContact.builder()
                .contactName("Contact One")
                .relationship("Friend")
                .phoneNumber("1234567890")
                .build();
        Patient patient1 = Patient.builder()
                .patientId("PAT-2026-90001")
                .medicalRecordNumber("MRN-901")
                .fullName("Stay Patient One")
                .age(25)
                .gender("Female")
                .dateOfBirth(LocalDate.of(2001, 1, 1))
                .mobileNumber("1112223334")
                .address("Street 1")
                .status("ACTIVE")
                .district(district)
                .healthCenter(center)
                .emergencyContact(contact1)
                .build();
        entityManager.persist(patient1);

        PatientEmergencyContact contact2 = PatientEmergencyContact.builder()
                .contactName("Contact Two")
                .relationship("Parent")
                .phoneNumber("0987654321")
                .build();
        Patient patient2 = Patient.builder()
                .patientId("PAT-2026-90002")
                .medicalRecordNumber("MRN-902")
                .fullName("Stay Patient Two")
                .age(40)
                .gender("Male")
                .dateOfBirth(LocalDate.of(1986, 1, 1))
                .mobileNumber("5556667778")
                .address("Street 2")
                .status("ACTIVE")
                .district(district)
                .healthCenter(center)
                .emergencyContact(contact2)
                .build();
        entityManager.persist(patient2);
        entityManager.flush();

        // 2. Create Ward with capacity = 1
        WardRequest wardRequest = new WardRequest(
                "W-ICU",
                "ICU Ward",
                "Emergency",
                2,
                1, // capacity = 1
                "ACTIVE",
                center.getId()
        );
        WardResponse wardResponse = wardService.createWard(wardRequest);
        Assertions.assertNotNull(wardResponse.id());
        Assertions.assertEquals(1, wardResponse.capacity());

        // 3. Create two Beds in the Ward
        BedRequest bedReqA = new BedRequest("ICU-01", "ICU", "AVAILABLE", "CLEAN", "FUNCTIONAL", wardResponse.id());
        BedResponse bedA = bedService.createBed(bedReqA);
        Assertions.assertNotNull(bedA.id());

        BedRequest bedReqB = new BedRequest("ICU-02", "ICU", "AVAILABLE", "CLEAN", "FUNCTIONAL", wardResponse.id());
        BedResponse bedB = bedService.createBed(bedReqB);
        Assertions.assertNotNull(bedB.id());

        // 4. Allocate Patient 1 to Bed A (Success)
        AllocationRequest allocReq1 = new AllocationRequest(
                patient1.getId(),
                bedA.id(),
                Instant.now(),
                null,
                "ADMITTED"
        );
        AllocationResponse allocResponse1 = allocationService.createAllocation(allocReq1);
        Assertions.assertNotNull(allocResponse1.id());
        Assertions.assertEquals("ADMITTED", allocResponse1.status());

        // Verify Bed A is marked OCCUPIED
        Bed updatedBedA = entityManager.find(Bed.class, bedA.id());
        Assertions.assertEquals("OCCUPIED", updatedBedA.getAvailabilityStatus());

        // Verify BedAllocatedEvent was published
        long allocEvents = applicationEvents.stream(BedAllocatedEvent.class).count();
        Assertions.assertEquals(1, allocEvents);

        // 5. Try to allocate Patient 2 to Bed B (Should fail due to capacity limit)
        AllocationRequest allocReq2 = new AllocationRequest(
                patient2.getId(),
                bedB.id(),
                Instant.now(),
                null,
                "ADMITTED"
        );
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            allocationService.createAllocation(allocReq2);
        });

        // Verify WardCapacityReachedEvent was published
        long capacityEvents = applicationEvents.stream(WardCapacityReachedEvent.class).count();
        Assertions.assertEquals(1, capacityEvents);

        // 6. Discharge Patient 1 from Bed A (Success)
        allocationService.dischargePatient(allocResponse1.id(), Instant.now());
        entityManager.flush();

        // Verify Bed A is marked AVAILABLE again
        entityManager.refresh(updatedBedA);
        Assertions.assertEquals("AVAILABLE", updatedBedA.getAvailabilityStatus());

        // Verify BedReleasedEvent was published
        long releasedEvents = applicationEvents.stream(BedReleasedEvent.class).count();
        Assertions.assertEquals(1, releasedEvents);

        // 7. Allocate Patient 1 to Bed A again (Success)
        AllocationResponse allocResponse1Retry = allocationService.createAllocation(allocReq1);
        Assertions.assertNotNull(allocResponse1Retry.id());

        // 8. Transfer Patient 1 from Bed A to Bed B
        TransferRequest transferRequest = new TransferRequest(
                patient1.getId(),
                bedB.id(),
                Instant.now(),
                "Needs specialized ICU hardware on Bed 2"
        );
        TransferResponse transferResponse = transferService.transferPatient(transferRequest);
        Assertions.assertNotNull(transferResponse.id());
        Assertions.assertEquals(bedA.id(), transferResponse.fromBedId());
        Assertions.assertEquals(bedB.id(), transferResponse.toBedId());
        entityManager.flush();

        // Verify Bed states: Bed A should be AVAILABLE, Bed B should be OCCUPIED
        entityManager.refresh(updatedBedA);
        Bed updatedBedB = entityManager.find(Bed.class, bedB.id());
        Assertions.assertEquals("AVAILABLE", updatedBedA.getAvailabilityStatus());
        Assertions.assertEquals("OCCUPIED", updatedBedB.getAvailabilityStatus());

        // Verify old allocation is DISCHARGED
        BedAllocation oldAlloc = entityManager.find(BedAllocation.class, allocResponse1Retry.id());
        Assertions.assertEquals("DISCHARGED", oldAlloc.getStatus());
        Assertions.assertNotNull(oldAlloc.getDischargeDate());
    }
}
