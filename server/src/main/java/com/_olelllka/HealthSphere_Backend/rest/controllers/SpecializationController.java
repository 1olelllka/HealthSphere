package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import com._olelllka.HealthSphere_Backend.service.SpecializationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
@Tag(name="Specializations", description = "Search for specializations. Requires authorization")
public class SpecializationController {

    private SpecializationMapper mapper;
    private SpecializationService service;

    @Autowired
    public SpecializationController(SpecializationMapper mapper, SpecializationService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @GetMapping("/specializations")
    @Operation(summary = "Get all specializations")
    public ResponseEntity<List<SpecializationDto>> getAllSpecializations() {
        List<SpecializationEntity> entities = service.getAllSpecializations();
        List<SpecializationDto> result = entities.stream().map(mapper::toDto).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
