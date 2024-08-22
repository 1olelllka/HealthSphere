package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    private PatientRepository patientRepository;
    private JwtService jwtService;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository, JwtService jwtService) {
        this.patientRepository = patientRepository;
        this.jwtService = jwtService;
    }

    @Override
    public PatientEntity getPatient(String jwt) {
        String username = jwtService.extractUsername(jwt);
        Optional<PatientEntity> patient = patientRepository.findByEmail(username);
        return patient.orElseThrow(() -> new NotFoundException("User with such username was not found."));
    }
}
