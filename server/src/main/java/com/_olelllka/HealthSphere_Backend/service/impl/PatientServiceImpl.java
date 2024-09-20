package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.PatientMessageProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {

    private PatientRepository patientRepository;
    private PatientElasticRepository elasticRepository;
    private PatientMessageProducer messageProducer;
    private JwtService jwtService;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository,
                              JwtService jwtService,
                              PatientElasticRepository elasticRepository,
                              PatientMessageProducer messageProducer) {
        this.patientRepository = patientRepository;
        this.jwtService = jwtService;
        this.elasticRepository = elasticRepository;
        this.messageProducer = messageProducer;
    }

    @Override
    public PatientEntity getPatient(String jwt) {
        String username = jwtService.extractUsername(jwt);
        Optional<PatientEntity> patient = patientRepository.findByEmail(username);
        return patient.orElseThrow(() -> new NotFoundException("User with such username was not found."));
    }

    @Override
    @Transactional
    public PatientEntity patchPatient(Long id, PatientEntity updatedPatientEntity) {
        return patientRepository.findById(id)
                .map(patient -> {
                    Optional.ofNullable(updatedPatientEntity.getAddress()).ifPresent(patient::setAddress);
                    Optional.ofNullable(updatedPatientEntity.getFirstName()).ifPresent(patient::setFirstName);
                    Optional.ofNullable(updatedPatientEntity.getLastName()).ifPresent(patient::setLastName);
                    Optional.ofNullable(updatedPatientEntity.getPhoneNumber()).ifPresent(patient::setPhoneNumber);
                    Optional.ofNullable(updatedPatientEntity.getDateOfBirth()).ifPresent(patient::setDateOfBirth);
                    Optional.ofNullable(updatedPatientEntity.getBloodType()).ifPresent(patient::setBloodType);
                    Optional.ofNullable(updatedPatientEntity.getAllergies()).ifPresent(patient::setAllergies);
                    PatientEntity result = patientRepository.save(patient);
                    PatientListDto patientListDto = PatientListDto.builder()
                            .firstName(patient.getFirstName())
                            .lastName(patient.getLastName())
                            .email(patient.getUser().getEmail())
                            .id(patient.getId())
                            .build();
                    messageProducer.sendMessageCreatePatient(patientListDto);
                    return result;
                }).orElseThrow(() -> new NotFoundException("Patient with such email was not found."));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        patientRepository.deleteById(id);
        messageProducer.sendMessageDeletePatient(id);
    }

    @Override
    public PatientEntity getPatientById(Long id) {
        return patientRepository.findById(id).orElseThrow(() -> new NotFoundException("User with such id was not found."));
    }

    @Override
    public Page<PatientDocument> getAllPatients(String params, Pageable pageable) {
        if (params.isEmpty()) {
            return elasticRepository.findAll(pageable);
        }
        return elasticRepository.findByParams(params, pageable);
    }
}
