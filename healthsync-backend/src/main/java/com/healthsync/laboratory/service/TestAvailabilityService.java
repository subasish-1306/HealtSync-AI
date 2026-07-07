package com.healthsync.laboratory.service;

import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.laboratory.dto.TestAvailabilityRequest;
import com.healthsync.laboratory.dto.TestAvailabilityResponse;
import com.healthsync.laboratory.entity.DiagnosticTest;
import com.healthsync.laboratory.entity.Laboratory;
import com.healthsync.laboratory.entity.TestAvailability;
import com.healthsync.laboratory.repository.DiagnosticTestRepository;
import com.healthsync.laboratory.repository.LaboratoryRepository;
import com.healthsync.laboratory.repository.TestAvailabilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Test Availabilities.
 */
@Service
@RequiredArgsConstructor
public class TestAvailabilityService {

    private final TestAvailabilityRepository testAvailabilityRepository;
    private final LaboratoryRepository laboratoryRepository;
    private final DiagnosticTestRepository diagnosticTestRepository;

    @Transactional(readOnly = true)
    public List<TestAvailabilityResponse> getAllAvailabilities() {
        return testAvailabilityRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestAvailabilityResponse> getAvailabilitiesByLab(UUID laboratoryId) {
        if (!laboratoryRepository.existsById(laboratoryId)) {
            throw new ResourceNotFoundException("Laboratory not found with ID: " + laboratoryId);
        }
        return testAvailabilityRepository.findByLaboratoryId(laboratoryId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestAvailabilityResponse getAvailabilityById(UUID id) {
        TestAvailability availability = testAvailabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Availability not found with ID: " + id));
        return mapToResponse(availability);
    }

    @Transactional
    public TestAvailabilityResponse createAvailability(TestAvailabilityRequest request) {
        Laboratory lab = laboratoryRepository.findById(request.laboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with ID: " + request.laboratoryId()));

        DiagnosticTest test = diagnosticTestRepository.findById(request.diagnosticTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic Test not found with ID: " + request.diagnosticTestId()));

        TestAvailability availability = TestAvailability.builder()
                .laboratory(lab)
                .diagnosticTest(test)
                .isAvailable(request.isAvailable())
                .turnaroundTimeHours(request.turnaroundTimeHours())
                .build();

        return mapToResponse(testAvailabilityRepository.save(availability));
    }

    @Transactional
    public TestAvailabilityResponse updateAvailability(UUID id, TestAvailabilityRequest request) {
        TestAvailability availability = testAvailabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Availability not found with ID: " + id));

        Laboratory lab = laboratoryRepository.findById(request.laboratoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Laboratory not found with ID: " + request.laboratoryId()));

        DiagnosticTest test = diagnosticTestRepository.findById(request.diagnosticTestId())
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic Test not found with ID: " + request.diagnosticTestId()));

        availability.setLaboratory(lab);
        availability.setDiagnosticTest(test);
        availability.setIsAvailable(request.isAvailable());
        availability.setTurnaroundTimeHours(request.turnaroundTimeHours());

        return mapToResponse(testAvailabilityRepository.save(availability));
    }

    @Transactional
    public void deleteAvailability(UUID id) {
        if (!testAvailabilityRepository.existsById(id)) {
            throw new ResourceNotFoundException("Test Availability not found with ID: " + id);
        }
        testAvailabilityRepository.deleteById(id);
    }

    private TestAvailabilityResponse mapToResponse(TestAvailability availability) {
        return new TestAvailabilityResponse(
                availability.getId(),
                availability.getLaboratory().getId(),
                availability.getLaboratory().getName(),
                availability.getDiagnosticTest().getId(),
                availability.getDiagnosticTest().getTestName(),
                availability.getIsAvailable(),
                availability.getTurnaroundTimeHours()
        );
    }
}
