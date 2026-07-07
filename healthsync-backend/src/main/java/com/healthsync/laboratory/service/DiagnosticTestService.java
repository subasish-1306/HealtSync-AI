package com.healthsync.laboratory.service;

import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.laboratory.dto.DiagnosticTestRequest;
import com.healthsync.laboratory.dto.DiagnosticTestResponse;
import com.healthsync.laboratory.entity.DiagnosticTest;
import com.healthsync.laboratory.repository.DiagnosticTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Diagnostic Tests (Laboratory Tests).
 */
@Service
@RequiredArgsConstructor
public class DiagnosticTestService {

    private final DiagnosticTestRepository diagnosticTestRepository;

    @Transactional(readOnly = true)
    public List<DiagnosticTestResponse> getAllTests() {
        return diagnosticTestRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiagnosticTestResponse getTestById(UUID id) {
        DiagnosticTest test = diagnosticTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic Test not found with ID: " + id));
        return mapToResponse(test);
    }

    @Transactional
    public DiagnosticTestResponse createTest(DiagnosticTestRequest request) {
        if (diagnosticTestRepository.findByCode(request.code()).isPresent()) {
            throw new IllegalArgumentException("Diagnostic Test already exists with code: " + request.code());
        }

        DiagnosticTest test = DiagnosticTest.builder()
                .testName(request.testName())
                .code(request.code())
                .description(request.description())
                .normalRange(request.normalRange())
                .baseCost(request.baseCost())
                .department(request.department())
                .duration(request.duration())
                .isAvailable(request.isAvailable())
                .build();

        return mapToResponse(diagnosticTestRepository.save(test));
    }

    @Transactional
    public DiagnosticTestResponse updateTest(UUID id, DiagnosticTestRequest request) {
        DiagnosticTest test = diagnosticTestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Diagnostic Test not found with ID: " + id));

        diagnosticTestRepository.findByCode(request.code()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Diagnostic Test already exists with code: " + request.code());
            }
        });

        test.setTestName(request.testName());
        test.setCode(request.code());
        test.setDescription(request.description());
        test.setNormalRange(request.normalRange());
        test.setBaseCost(request.baseCost());
        test.setDepartment(request.department());
        test.setDuration(request.duration());
        test.setIsAvailable(request.isAvailable());

        return mapToResponse(diagnosticTestRepository.save(test));
    }

    @Transactional
    public void deleteTest(UUID id) {
        if (!diagnosticTestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Diagnostic Test not found with ID: " + id);
        }
        diagnosticTestRepository.deleteById(id);
    }

    private DiagnosticTestResponse mapToResponse(DiagnosticTest test) {
        return new DiagnosticTestResponse(
                test.getId(),
                test.getTestName(),
                test.getCode(),
                test.getDescription(),
                test.getNormalRange(),
                test.getBaseCost(),
                test.getDepartment(),
                test.getDuration(),
                test.getIsAvailable()
        );
    }
}
