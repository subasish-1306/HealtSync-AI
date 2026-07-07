package com.healthsync.ai.dto;

import java.util.List;

/**
 * Data Transfer Object consolidating AI predictions across resources.
 */
public record PredictionSummary(
    List<PatientLoadPrediction> patientLoad,
    List<BedOccupancyPrediction> bedOccupancy,
    List<DoctorShortagePrediction> doctorShortages,
    List<MedicineStockPrediction> medicineStockOuts
) {
    public record PatientLoadPrediction(String period, int predictedVisits, double confidenceScore) {}
    public record BedOccupancyPrediction(int predictedOccupiedBeds, int availableBeds, int emergencyCapacity, double confidenceScore) {}
    public record DoctorShortagePrediction(String department, int currentDoctors, int predictedNeeded, String shortageRisk) {}
    public record MedicineStockPrediction(String medicineName, int currentStock, int averageDailyUsage, int daysRemaining, String riskLevel) {}
}
