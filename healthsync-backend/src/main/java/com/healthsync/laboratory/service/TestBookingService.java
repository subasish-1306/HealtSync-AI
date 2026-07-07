package com.healthsync.laboratory.service;

import com.healthsync.doctor.entity.Doctor;
import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.laboratory.dto.BookingRequest;
import com.healthsync.laboratory.dto.BookingResponse;
import com.healthsync.laboratory.entity.DiagnosticTest;
import com.healthsync.laboratory.entity.TestBooking;
import com.healthsync.laboratory.repository.DiagnosticTestRepository;
import com.healthsync.laboratory.repository.TestBookingRepository;
import com.healthsync.patient.entity.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Test Bookings.
 */
@Service
@RequiredArgsConstructor
public class TestBookingService {

    private final TestBookingRepository testBookingRepository;
    private final DiagnosticTestRepository diagnosticTestRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        return testBookingRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByPatient(UUID patientId) {
        return testBookingRepository.findByPatientId(patientId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(UUID id) {
        TestBooking booking = testBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Booking not found with ID: " + id));
        return mapToResponse(booking);
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        Patient patient = entityManager.find(Patient.class, request.patientId());
        if (patient == null) {
            throw new ResourceNotFoundException("Patient not found with ID: " + request.patientId());
        }

        Doctor doctor = entityManager.find(Doctor.class, request.doctorId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + request.doctorId());
        }

        DiagnosticTest test = diagnosticTestRepository.findById(request.diagnosticTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic Test not found with ID: " + request.diagnosticTestId()));

        TestBooking booking = TestBooking.builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosticTest(test)
                .bookingDate(request.bookingDate())
                .status(request.status())
                .priority(request.priority())
                .build();

        return mapToResponse(testBookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse updateBooking(UUID id, BookingRequest request) {
        TestBooking booking = testBookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Booking not found with ID: " + id));

        Patient patient = entityManager.find(Patient.class, request.patientId());
        if (patient == null) {
            throw new ResourceNotFoundException("Patient not found with ID: " + request.patientId());
        }

        Doctor doctor = entityManager.find(Doctor.class, request.doctorId());
        if (doctor == null) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + request.doctorId());
        }

        DiagnosticTest test = diagnosticTestRepository.findById(request.diagnosticTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic Test not found with ID: " + request.diagnosticTestId()));

        booking.setPatient(patient);
        booking.setDoctor(doctor);
        booking.setDiagnosticTest(test);
        booking.setBookingDate(request.bookingDate());
        booking.setStatus(request.status());
        booking.setPriority(request.priority());

        return mapToResponse(testBookingRepository.save(booking));
    }

    @Transactional
    public void deleteBooking(UUID id) {
        if (!testBookingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Test Booking not found with ID: " + id);
        }
        testBookingRepository.deleteById(id);
    }

    private BookingResponse mapToResponse(TestBooking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getPatient().getId(),
                booking.getPatient().getFullName(),
                booking.getDoctor().getId(),
                booking.getDoctor().getUser().getFullName(),
                booking.getDiagnosticTest().getId(),
                booking.getDiagnosticTest().getTestName(),
                booking.getBookingDate(),
                booking.getStatus(),
                booking.getPriority()
        );
    }
}
