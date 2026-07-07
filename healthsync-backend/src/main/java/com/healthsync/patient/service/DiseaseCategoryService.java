package com.healthsync.patient.service;

import com.healthsync.exception.ResourceNotFoundException;
import com.healthsync.patient.dto.DiseaseCategoryRequest;
import com.healthsync.patient.dto.DiseaseCategoryResponse;
import com.healthsync.patient.entity.DiseaseCategory;
import com.healthsync.patient.repository.DiseaseCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class handling business logic for Disease Categories.
 */
@Service
@RequiredArgsConstructor
public class DiseaseCategoryService {

    private final DiseaseCategoryRepository diseaseCategoryRepository;

    @Transactional(readOnly = true)
    public List<DiseaseCategoryResponse> getAllCategories() {
        return diseaseCategoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DiseaseCategoryResponse getCategoryById(UUID id) {
        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease Category not found with ID: " + id));
        return mapToResponse(category);
    }

    @Transactional
    public DiseaseCategoryResponse createCategory(DiseaseCategoryRequest request) {
        if (diseaseCategoryRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Disease Category already exists with code: " + request.code());
        }

        DiseaseCategory category = DiseaseCategory.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .priority(request.priority())
                .communicable(request.communicable())
                .seasonal(request.seasonal())
                .build();

        DiseaseCategory saved = diseaseCategoryRepository.save(category);
        return mapToResponse(saved);
    }

    @Transactional
    public DiseaseCategoryResponse updateCategory(UUID id, DiseaseCategoryRequest request) {
        DiseaseCategory category = diseaseCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease Category not found with ID: " + id));

        if (!category.getCode().equals(request.code()) && diseaseCategoryRepository.existsByCode(request.code())) {
            throw new IllegalArgumentException("Disease Category already exists with code: " + request.code());
        }

        category.setCode(request.code());
        category.setName(request.name());
        category.setDescription(request.description());
        category.setPriority(request.priority());
        category.setCommunicable(request.communicable());
        category.setSeasonal(request.seasonal());

        DiseaseCategory updated = diseaseCategoryRepository.save(category);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (!diseaseCategoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Disease Category not found with ID: " + id);
        }
        diseaseCategoryRepository.deleteById(id);
    }

    private DiseaseCategoryResponse mapToResponse(DiseaseCategory category) {
        return new DiseaseCategoryResponse(
                category.getId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.getPriority(),
                category.getCommunicable(),
                category.getSeasonal()
        );
    }
}
