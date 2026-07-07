package com.healthsync.patient.service;

import com.healthsync.common.event.VisitCompletedEvent;
import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.district.entity.HealthCenter;
import com.healthsync.doctor.entity.Doctor;
import com.healthsync.patient.dto.VisitRequest;
import com.healthsync.patient.dto.VisitResponse;
import com.healthsync.patient.entity.DiseaseCategory;
import com.healthsync.patient.entity.Patient;
import com.healthsync.patient.entity.PatientVisit;
import com.healthsync.patient.repository.PatientRepository;
import com.healthsync.patient.repository.VisitRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Patient Visits.
 */
@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final PatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<VisitResponse> searchVisits(UUID patientId, UUID healthCenterId, String department, Instant startDate, Instant endDate) {
        return visitRepository.searchVisits(
                patientId,
                healthCenterId,
                (department != null && !department.isBlank()) ? department : null,
                startDate,
                endDate
        ).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VisitResponse getVisitById(UUID id) {
        PatientVisit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient Visit not found with ID: " + id));
        return mapToResponse(visit);
    }

    @Transactional
    public VisitResponse createVisit(VisitRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        HealthCenter healthCenter = entityManager.find(HealthCenter.class, request.healthCenterId());
        if (healthCenter == null) {
            throw new ResourceNotFoundException("Health Center not found with ID: " + request.healthCenterId());
        }

        DiseaseCategory diseaseCategory = entityManager.find(DiseaseCategory.class, request.diseaseCategoryId());
        if (diseaseCategory == null) {
            throw new ResourceNotFoundException("Disease Category not found with ID: " + request.diseaseCategoryId());
        }

        Doctor doctor = null;
        if (request.doctorId() != null) {
            doctor = entityManager.find(Doctor.class, request.doctorId());
            if (doctor == null) {
                throw new ResourceNotFoundException("Doctor not found with ID: " + request.doctorId());
            }
        }

        String ageGroup = calculateAgeGroup(patient.getAge());

        PatientVisit visit = PatientVisit.builder()
                .patient(patient)
                .healthCenter(healthCenter)
                .doctor(doctor)
                .visitDate(request.visitDate())
                .visitTime(request.visitTime())
                .department(request.department())
                .visitType(request.visitType())
                .symptoms(request.symptoms())
                .diagnosis(request.diagnosis())
                .prescriptionReference(request.prescriptionReference())
                .status(request.status())
                .visitDuration(request.visitDuration())
                .waitingTime(request.waitingTime())
                .ageGroup(ageGroup)
                .diseaseCategory(diseaseCategory)
                .build();

        PatientVisit saved = visitRepository.save(visit);

        if ("COMPLETED".equalsIgnoreCase(saved.getStatus())) {
            publishVisitCompleted(saved);
        }

        return mapToResponse(saved);
    }

    @Transactional
    public VisitResponse updateVisit(UUID id, VisitRequest request) {
        PatientVisit visit = visitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient Visit not found with ID: " + id));

        Patient patient = patientRepository.findById(request.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.patientId()));

        HealthCenter healthCenter = entityManager.find(HealthCenter.class, request.healthCenterId());
        if (healthCenter == null) {
            throw new ResourceNotFoundException("Health Center not found with ID: " + request.healthCenterId());
        }

        DiseaseCategory diseaseCategory = entityManager.find(DiseaseCategory.class, request.diseaseCategoryId());
        if (diseaseCategory == null) {
            throw new ResourceNotFoundException("Disease Category not found with ID: " + request.diseaseCategoryId());
        }

        Doctor doctor = null;
        if (request.doctorId() != null) {
            doctor = entityManager.find(Doctor.class, request.doctorId());
            if (doctor == null) {
                throw new ResourceNotFoundException("Doctor not found with ID: " + request.doctorId());
            }
        }

        String ageGroup = calculateAgeGroup(patient.getAge());
        String oldStatus = visit.getStatus();

        visit.setPatient(patient);
        visit.setHealthCenter(healthCenter);
        visit.setDoctor(doctor);
        visit.setVisitDate(request.visitDate());
        visit.setVisitTime(request.visitTime());
        visit.setDepartment(request.department());
        visit.setVisitType(request.visitType());
        visit.setSymptoms(request.symptoms());
        visit.setDiagnosis(request.diagnosis());
        visit.setPrescriptionReference(request.prescriptionReference());
        visit.setStatus(request.status());
        visit.setVisitDuration(request.visitDuration());
        visit.setWaitingTime(request.waitingTime());
        visit.setAgeGroup(ageGroup);
        visit.setDiseaseCategory(diseaseCategory);

        PatientVisit updated = visitRepository.save(visit);

        if (!"COMPLETED".equalsIgnoreCase(oldStatus) && "COMPLETED".equalsIgnoreCase(updated.getStatus())) {
            publishVisitCompleted(updated);
        }

        return mapToResponse(updated);
    }

    @Transactional
    public void deleteVisit(UUID id) {
        if (!visitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient Visit not found with ID: " + id);
        }
        visitRepository.deleteById(id);
    }

    private String calculateAgeGroup(int age) {
        if (age < 18) {
            return "CHILD";
        } else if (age >= 60) {
            return "SENIOR";
        } else {
            return "ADULT";
        }
    }

    private void publishVisitCompleted(PatientVisit visit) {
        eventPublisher.publishEvent(new VisitCompletedEvent(
                visit.getId(),
                visit.getPatient().getId(),
                visit.getPatient().getPatientId(),
                visit.getPatient().getGender(),
                visit.getPatient().getAge(),
                visit.getHealthCenter().getId(),
                visit.getDepartment(),
                visit.getVisitDuration(),
                visit.getWaitingTime(),
                visit.getAgeGroup(),
                visit.getDiseaseCategory().getCode(),
                visit.getDiseaseCategory().getCommunicable()
        ));
    }

    private VisitResponse mapToResponse(PatientVisit visit) {
        UUID doctorId = visit.getDoctor() != null ? visit.getDoctor().getId() : null;

        return new VisitResponse(
                visit.getId(),
                visit.getPatient().getId(),
                visit.getPatient().getPatientId(),
                visit.getPatient().getFullName(),
                visit.getHealthCenter().getId(),
                visit.getHealthCenter().getName(),
                doctorId,
                visit.getVisitDate(),
                visit.getVisitTime(),
                visit.getDepartment(),
                visit.getVisitType(),
                visit.getSymptoms(),
                visit.getDiagnosis(),
                visit.getPrescriptionReference(),
                visit.getStatus(),
                visit.getVisitDuration(),
                visit.getWaitingTime(),
                visit.getAgeGroup(),
                visit.getDiseaseCategory().getId(),
                visit.getDiseaseCategory().getCode(),
                visit.getDiseaseCategory().getName()
        );
    }
}
