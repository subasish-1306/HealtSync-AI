package com.healthsync.laboratory.service;

import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.laboratory.dto.ResultRequest;
import com.healthsync.laboratory.dto.ResultResponse;
import com.healthsync.laboratory.entity.TestBooking;
import com.healthsync.laboratory.entity.TestResult;
import com.healthsync.laboratory.repository.TestBookingRepository;
import com.healthsync.laboratory.repository.TestResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Test Results.
 */
@Service
@RequiredArgsConstructor
public class TestResultService {

    private final TestResultRepository testResultRepository;
    private final TestBookingRepository testBookingRepository;

    @Transactional(readOnly = true)
    public List<ResultResponse> getAllResults() {
        return testResultRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ResultResponse getResultByBooking(UUID bookingId) {
        TestResult result = testResultRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Test Result not found for booking: " + bookingId));
        return mapToResponse(result);
    }

    @Transactional(readOnly = true)
    public ResultResponse getResultById(UUID id) {
        TestResult result = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Result not found with ID: " + id));
        return mapToResponse(result);
    }

    @Transactional
    public ResultResponse createResult(ResultRequest request) {
        TestBooking booking = testBookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Test Booking not found with ID: " + request.bookingId()));

        if (testResultRepository.findByBookingId(request.bookingId()).isPresent()) {
            throw new IllegalArgumentException("Test Result already uploaded for booking: " + request.bookingId());
        }

        booking.setStatus("COMPLETED");
        testBookingRepository.save(booking);

        TestResult result = TestResult.builder()
                .booking(booking)
                .result(request.result())
                .completedDate(Instant.now())
                .remarks(request.remarks())
                .build();

        return mapToResponse(testResultRepository.save(result));
    }

    @Transactional
    public ResultResponse updateResult(UUID id, ResultRequest request) {
        TestResult result = testResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Test Result not found with ID: " + id));

        TestBooking booking = testBookingRepository.findById(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Test Booking not found with ID: " + request.bookingId()));

        result.setBooking(booking);
        result.setResult(request.result());
        result.setRemarks(request.remarks());

        return mapToResponse(testResultRepository.save(result));
    }

    @Transactional
    public void deleteResult(UUID id) {
        if (!testResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Test Result not found with ID: " + id);
        }
        testResultRepository.deleteById(id);
    }

    private ResultResponse mapToResponse(TestResult result) {
        return new ResultResponse(
                result.getId(),
                result.getBooking().getId(),
                result.getResult(),
                result.getCompletedDate(),
                result.getRemarks()
        );
    }
}
