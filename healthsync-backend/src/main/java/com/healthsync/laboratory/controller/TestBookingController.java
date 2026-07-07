package com.healthsync.laboratory.controller;

import com.healthsync.laboratory.dto.BookingRequest;
import com.healthsync.laboratory.dto.BookingResponse;
import com.healthsync.laboratory.service.TestBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing endpoints for patient diagnostic bookings.
 */
@RestController
@RequestMapping("/api/test-bookings")
@RequiredArgsConstructor
@Tag(name = "Test Bookings", description = "Endpoints for managing patient test bookings")
public class TestBookingController {

    private final TestBookingService testBookingService;

    @GetMapping
    @Operation(summary = "Get all test bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(testBookingService.getAllBookings());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get bookings by Patient ID")
    public ResponseEntity<List<BookingResponse>> getBookingsByPatient(@PathVariable UUID patientId) {
        return ResponseEntity.ok(testBookingService.getBookingsByPatient(patientId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(testBookingService.getBookingById(id));
    }

    @PostMapping
    @Operation(summary = "Book a diagnostic test")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testBookingService.createBooking(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update booking details")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable UUID id,
            @Valid @RequestBody BookingRequest request
    ) {
        return ResponseEntity.ok(testBookingService.updateBooking(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel / Delete a booking")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID id) {
        testBookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }
}
