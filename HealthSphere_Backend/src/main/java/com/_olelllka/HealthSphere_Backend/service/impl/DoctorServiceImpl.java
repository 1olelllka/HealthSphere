package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.DoctorService;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private DoctorRepository repository;
    private JwtService jwtService;

    @Autowired
    public DoctorServiceImpl(DoctorRepository repository,
                             JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Override
    public Page<DoctorEntity> getAllDoctors(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public DoctorEntity getDoctorByEmail(String jwt) {
        String email = jwtService.extractUsername(jwt);
        return repository.findByUserEmail(email).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
    }

    @Override
    public DoctorEntity getDoctorById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Doctor with such id was not found."));
    }

    @Override
    public DoctorEntity patchDoctor(String jwt, DoctorEntity updatedDoctor) {
        String email = jwtService.extractUsername(jwt);
        return repository.findByUserEmail(email).map(doctor -> {
            Optional.ofNullable(updatedDoctor.getFirstName()).ifPresent(doctor::setFirstName);
            Optional.ofNullable(updatedDoctor.getLastName()).ifPresent(doctor::setLastName);
            Optional.ofNullable(updatedDoctor.getExperienceYears()).ifPresent(doctor::setExperienceYears);
            Optional.ofNullable(updatedDoctor.getPhoneNumber()).ifPresent(doctor::setPhoneNumber);
            Optional.ofNullable(updatedDoctor.getClinicAddress()).ifPresent(doctor::setClinicAddress);
            Optional.ofNullable(updatedDoctor.getLicenseNumber()).ifPresent(doctor::setLicenseNumber);
            return repository.save(doctor);
        }).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
    }

    @Override
    public void deleteDoctorByEmail(String jwt) {
        String email = jwtService.extractUsername(jwt);
        DoctorEntity doctor = repository.findByUserEmail(email).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
        repository.deleteById(doctor.getId());
    }
}
