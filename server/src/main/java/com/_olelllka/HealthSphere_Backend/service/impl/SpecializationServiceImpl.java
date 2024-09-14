package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.repositories.SpecializationRepository;
import com._olelllka.HealthSphere_Backend.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SpecializationServiceImpl implements SpecializationService {

    private SpecializationRepository repository;

    @Autowired
    public SpecializationServiceImpl(SpecializationRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<SpecializationEntity> getAllSpecializations(Pageable pageable) {
        return repository.findAll(pageable);
    }
}