package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpecializationService {
    Page<SpecializationEntity> getAllSpecializations(Pageable pageable);
}
