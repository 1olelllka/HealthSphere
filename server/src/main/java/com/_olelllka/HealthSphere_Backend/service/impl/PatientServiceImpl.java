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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PatientServiceImpl implements PatientService {

    private PatientRepository patientRepository;
    private PatientElasticRepository elasticRepository;
    private PatientMessageProducer messageProducer;
    private JwtService jwtService;
    private PatientHelperCachingService patientHelperCachingService;
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public PatientServiceImpl(PatientRepository patientRepository,
                              JwtService jwtService,
                              PatientElasticRepository elasticRepository,
                              PatientHelperCachingService patientHelperCachingService,
                              RedisTemplate<String, String> redisTemplate,
                              PatientMessageProducer messageProducer) {
        this.patientRepository = patientRepository;
        this.jwtService = jwtService;
        this.elasticRepository = elasticRepository;
        this.patientHelperCachingService = patientHelperCachingService;
        this.messageProducer = messageProducer;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public PatientEntity getPatient(String jwt) {
        String username = jwtService.extractUsername(jwt);
        return patientHelperCachingService.getPatientByUsername(username);
    }

    @Override
    @Transactional
    public PatientEntity patchPatient(String jwt, PatientEntity updatedPatientEntity) {
        String email = jwtService.extractUsername(jwt);
        PatientEntity result =  patientHelperCachingService.patchPatientByEmail(email, updatedPatientEntity);
        PatientListDto patientListDto = PatientListDto.builder()
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .email(result.getUser().getEmail())
                .id(result.getId())
                .build();
        messageProducer.sendMessageCreatePatient(patientListDto);
        return result;
    }

    @Override
    @Transactional
    public void deletePatient(String jwt) {
        String email = jwtService.extractUsername(jwt);
        PatientEntity patient = patientHelperCachingService.getPatientByUsername(email);
        patientHelperCachingService.deletePatientByEmail(email);
        patientRepository.deleteById(patient.getId());
        messageProducer.sendMessageDeletePatient(patient.getId());
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
