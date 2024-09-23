package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorHelperCachingService {

    @Autowired
    private DoctorRepository repository;

    @Cacheable(value = "profile", keyGenerator = "sha256KeyGenerator")
    public DoctorEntity getDoctorByEmail(String email) {
        return repository.findByUserEmail(email).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
    }

    @CachePut(value="profile", keyGenerator = "sha256KeyGenerator")
    public DoctorEntity updateDoctorByEmail(String email, DoctorEntity updatedDoctor) {
        return repository.findByUserEmail(email).map(doctor -> {
            Optional.ofNullable(updatedDoctor.getFirstName()).ifPresent(doctor::setFirstName);
            Optional.ofNullable(updatedDoctor.getLastName()).ifPresent(doctor::setLastName);
            Optional.ofNullable(updatedDoctor.getExperienceYears()).ifPresent(doctor::setExperienceYears);
            Optional.ofNullable(updatedDoctor.getPhoneNumber()).ifPresent(doctor::setPhoneNumber);
            Optional.ofNullable(updatedDoctor.getClinicAddress()).ifPresent(doctor::setClinicAddress);
            Optional.ofNullable(updatedDoctor.getLicenseNumber()).ifPresent(doctor::setLicenseNumber);
            Optional.ofNullable(updatedDoctor.getSpecializations()).ifPresent(doctor::setSpecializations);
            return repository.save(doctor);
        }).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
    }

    @CacheEvict(value="profile", keyGenerator = "sha256KeyGenerator")
    public void deleteDoctorByEmail(String email) {}

}
