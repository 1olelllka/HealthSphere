package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
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

    @Override
    public PatientEntity patchPatient(String jwt, PatientEntity updatedPatientEntity) {
        String username = jwtService.extractUsername(jwt);
        return patientRepository.findByEmail(username)
                .map(patient -> {
                    Optional.ofNullable(updatedPatientEntity.getAddress()).ifPresent(patient::setAddress);
                    Optional.ofNullable(updatedPatientEntity.getFirstName()).ifPresent(patient::setFirstName);
                    Optional.ofNullable(updatedPatientEntity.getLastName()).ifPresent(patient::setLastName);
                    Optional.ofNullable(updatedPatientEntity.getPhoneNumber()).ifPresent(patient::setPhoneNumber);
                    Optional.ofNullable(updatedPatientEntity.getDateOfBirth()).ifPresent(patient::setDateOfBirth);
                    return patientRepository.save(patient);
                }).orElseThrow(() -> new NotFoundException("Patient with such email was not found."));
    }
}
