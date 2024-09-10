package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import com._olelllka.HealthSphere_Backend.service.SpecializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1")
public class SpecializationController {

    private SpecializationMapper mapper;
    private SpecializationService service;

    @Autowired
    public SpecializationController(SpecializationMapper mapper, SpecializationService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/specializations")
    public ResponseEntity<Page<SpecializationDto>> getAllSpecializations(Pageable pageable) {
        Page<SpecializationEntity> entities = service.getAllSpecializations(pageable);
        Page<SpecializationDto> result = entities.map(mapper::toDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
