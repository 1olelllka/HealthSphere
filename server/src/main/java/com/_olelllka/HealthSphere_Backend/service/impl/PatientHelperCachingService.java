package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientHelperCachingService {

    private static final Logger log = LoggerFactory.getLogger(PatientHelperCachingService.class);
    @Autowired
    private PatientRepository patientRepository;

    @Cacheable(value = "profile", keyGenerator = "sha256KeyGenerator")
    public PatientEntity getPatientByUsername(String username) {
        log.info("not a cache");
        Optional<PatientEntity> patient = patientRepository.findByEmail(username);
        return patient.orElseThrow(() -> new NotFoundException("User with such username was not found."));
    }

    @CachePut(value="profile", keyGenerator = "sha256KeyGenerator")
    public PatientEntity patchPatientByEmail(String email, PatientEntity updated) {
        return patientRepository.findByEmail(email)
                .map(patient -> {
                    Optional.ofNullable(updated.getAddress()).ifPresent(patient::setAddress);
                    Optional.ofNullable(updated.getFirstName()).ifPresent(patient::setFirstName);
                    Optional.ofNullable(updated.getLastName()).ifPresent(patient::setLastName);
                    Optional.ofNullable(updated.getPhoneNumber()).ifPresent(patient::setPhoneNumber);
                    Optional.ofNullable(updated.getDateOfBirth()).ifPresent(patient::setDateOfBirth);
                    Optional.ofNullable(updated.getBloodType()).ifPresent(patient::setBloodType);
                    Optional.ofNullable(updated.getAllergies()).ifPresent(patient::setAllergies);
                    return patientRepository.save(patient);
                }).orElseThrow(() -> new NotFoundException("Patient with such email was not found."));
    }

    @CacheEvict(value = "profile", keyGenerator = "sha256KeyGenerator")
    public void deletePatientByEmail(String email) {
    }
}
