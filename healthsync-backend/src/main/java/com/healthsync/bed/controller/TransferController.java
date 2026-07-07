package com.healthsync.bed.controller;

import com.healthsync.bed.dto.TransferRequest;
import com.healthsync.bed.dto.TransferResponse;
import com.healthsync.bed.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller exposing endpoints for Patient Bed Transfers.
 */
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Bed Transfers", description = "Endpoints for managing patient bed transfers")
public class TransferController {

    private final TransferService transferService;

    @GetMapping
    @Operation(summary = "Get all transfer logs")
    public ResponseEntity<List<TransferResponse>> getAllTransfers() {
        return ResponseEntity.ok(transferService.getAllTransfers());
    }

    @PostMapping
    @Operation(summary = "Transfer a patient to a different bed")
    public ResponseEntity<TransferResponse> transferPatient(@Valid @RequestBody TransferRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transferService.transferPatient(request));
    }
}
