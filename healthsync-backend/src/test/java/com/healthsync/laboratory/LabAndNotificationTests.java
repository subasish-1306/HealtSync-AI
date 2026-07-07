package com.healthsync.laboratory;

import com.healthsync.auth.entity.Role;
import com.healthsync.auth.entity.User;
import com.healthsync.auth.entity.UserStatus;
import com.healthsync.district.entity.District;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.district.entity.HealthCenterType;
import com.healthsync.doctor.entity.Doctor;
import com.healthsync.doctor.entity.Shift;
import com.healthsync.laboratory.dto.*;
import com.healthsync.laboratory.service.*;
import com.healthsync.notification.dto.NotificationResponse;
import com.healthsync.notification.dto.PreferenceRequest;
import com.healthsync.notification.dto.PreferenceResponse;
import com.healthsync.notification.service.NotificationService;
import com.healthsync.patient.entity.Patient;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Integration test suite for the Laboratory and Smart Notification modules.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class LabAndNotificationTests {

    @Autowired
    private LaboratoryService laboratoryService;

    @Autowired
    private DiagnosticTestService diagnosticTestService;

    @Autowired
    private TestAvailabilityService testAvailabilityService;

    @Autowired
    private TestBookingService testBookingService;

    @Autowired
    private TestResultService testResultService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testCompleteLabAndNotificationWorkflow() {
        // Ensure patient sequence exists for H2
        entityManager.createNativeQuery("CREATE SEQUENCE IF NOT EXISTS patient_id_seq START WITH 1 INCREMENT BY 1").executeUpdate();

        // 1. Setup foundations
        District district = District.builder()
                .name("Lab Test District")
                .region("West")
                .population(200000L)
                .build();
        district.setCreatedBy("admin");
        district.setUpdatedBy("admin");
        entityManager.persist(district);

        HealthCenter center = HealthCenter.builder()
                .name("Lab Clinic")
                .type(HealthCenterType.PHC)
                .address("777 Lab Lane")
                .capacity(50)
                .district(district)
                .build();
        center.setCreatedBy("admin");
        center.setUpdatedBy("admin");
        entityManager.persist(center);

        User user = User.builder()
                .username("doctor_lab")
                .password("secretpass")
                .fullName("Dr. Lab Assistant")
                .email("doctor@lab.com")
                .role(Role.DOCTOR)
                .status(UserStatus.ACTIVE)
                .build();
        user.setCreatedBy("admin");
        user.setUpdatedBy("admin");
        entityManager.persist(user);

        Shift shift = Shift.builder()
                .name("Lab Morning Shift")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(16, 0))
                .build();
        shift.setCreatedBy("admin");
        shift.setUpdatedBy("admin");
        entityManager.persist(shift);

        Doctor doctor = Doctor.builder()
                .user(user)
                .healthCenter(center)
                .specialization("General Pathology")
                .licenseNumber("LIC-LAB-999")
                .defaultShift(shift)
                .build();
        doctor.setCreatedBy("admin");
        doctor.setUpdatedBy("admin");
        entityManager.persist(doctor);

        Patient patient = Patient.builder()
                .fullName("John LabPatient")
                .patientId("PT-LAB-101")
                .medicalRecordNumber("MRN-LAB-101")
                .age(40)
                .gender("MALE")
                .dateOfBirth(LocalDate.of(1986, 5, 12))
                .bloodGroup("O+")
                .mobileNumber("9988776655")
                .address("Tenth avenue")
                .district(district)
                .healthCenter(center)
                .status("ACTIVE")
                .build();
        patient.setCreatedBy("admin");
        patient.setUpdatedBy("admin");
        entityManager.persist(patient);

        entityManager.flush();

        // 2. Create Laboratory
        LaboratoryRequest labReq = new LaboratoryRequest(
                "Biochem lab",
                center.getId(),
                "ACTIVE",
                "09:00 - 18:00",
                8
        );
        LaboratoryResponse labRes = laboratoryService.createLaboratory(labReq);
        Assertions.assertNotNull(labRes.id());
        Assertions.assertEquals("Biochem lab", labRes.name());

        // 3. Create DiagnosticTest (Laboratory Test)
        DiagnosticTestRequest testReq = new DiagnosticTestRequest(
                "Blood Sugar FBG",
                "TEST-FBG",
                "Fasting Blood Glucose",
                "70-100 mg/dL",
                BigDecimal.valueOf(150.00),
                "Pathology",
                20,
                true
        );
        DiagnosticTestResponse testRes = diagnosticTestService.createTest(testReq);
        Assertions.assertNotNull(testRes.id());
        Assertions.assertEquals("TEST-FBG", testRes.code());

        // 4. Define Test Availability inside lab
        TestAvailabilityRequest availReq = new TestAvailabilityRequest(
                labRes.id(),
                testRes.id(),
                true,
                2
        );
        TestAvailabilityResponse availRes = testAvailabilityService.createAvailability(availReq);
        Assertions.assertNotNull(availRes.id());
        Assertions.assertEquals(2, availRes.turnaroundTimeHours());

        // 5. Create Test Booking
        BookingRequest bookingReq = new BookingRequest(
                patient.getId(),
                doctor.getId(),
                testRes.id(),
                LocalDate.now(),
                "PENDING",
                "ROUTINE"
        );
        BookingResponse bookingRes = testBookingService.createBooking(bookingReq);
        Assertions.assertNotNull(bookingRes.id());
        Assertions.assertEquals("PENDING", bookingRes.status());

        // 6. Upload Test Results (checks booking transition to COMPLETED)
        ResultRequest resultReq = new ResultRequest(
                bookingRes.id(),
                "95 mg/dL",
                "Normal fasting sugar range."
        );
        ResultResponse resultRes = testResultService.createResult(resultReq);
        Assertions.assertNotNull(resultRes.id());
        Assertions.assertEquals("95 mg/dL", resultRes.result());

        // Verify booking status transitioned to COMPLETED
        BookingResponse updatedBooking = testBookingService.getBookingById(bookingRes.id());
        Assertions.assertEquals("COMPLETED", updatedBooking.status());

        // 7. Configure Notification preferences
        PreferenceRequest prefReq = new PreferenceRequest(
                user.getId(),
                "MEDICINE_LOW_STOCK",
                "EMAIL",
                true
        );
        PreferenceResponse prefRes = notificationService.updatePreference(prefReq);
        Assertions.assertNotNull(prefRes.id());
        Assertions.assertTrue(prefRes.enabled());

        // 8. Trigger alert dispatch
        NotificationResponse notifRes = notificationService.triggerNotification(
                user.getId(),
                center.getId(),
                "Paracetamol stock is low!",
                "MEDICINE_LOW_STOCK"
        );
        Assertions.assertNotNull(notifRes.id());
        Assertions.assertEquals("UNREAD", notifRes.status());

        // Verify notifications list retrieved successfully
        List<NotificationResponse> list = notificationService.getNotificationsForUser(user.getId());
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals("Paracetamol stock is low!", list.get(0).message());

        // 9. Mark read
        NotificationResponse readRes = notificationService.markAsRead(notifRes.id());
        Assertions.assertEquals("READ", readRes.status());
    }
}
