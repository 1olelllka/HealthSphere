package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.DoctorMessageProducer;
import com._olelllka.HealthSphere_Backend.service.DoctorService;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private DoctorRepository repository;
    private DoctorElasticRepository elasticRepository;
    private JwtService jwtService;
    private DoctorMessageProducer messageProducer;
    private SpecializationMapper specializationMapper;
    private DoctorHelperCachingService cachingService;

    @Autowired
    public DoctorServiceImpl(DoctorRepository repository,
                             JwtService jwtService,
                             DoctorMessageProducer messageProducer,
                             DoctorElasticRepository elasticRepository,
                             DoctorHelperCachingService cachingService,
                             SpecializationMapper specializationMapper) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.messageProducer = messageProducer;
        this.elasticRepository = elasticRepository;
        this.specializationMapper = specializationMapper;
        this.cachingService = cachingService;
    }

    @Override
    public Page<DoctorEntity> getAllDoctors(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public DoctorEntity getDoctorByEmail(String jwt) {
        String email = jwtService.extractUsername(jwt);
        return cachingService.getDoctorByEmail(email);
    }

    @Override
    public DoctorEntity getDoctorById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Doctor with such id was not found."));
    }

    @Transactional
    @Override
    public DoctorEntity patchDoctor(String jwt, DoctorEntity updatedDoctor) {
        String email = jwtService.extractUsername(jwt);
        DoctorEntity result = cachingService.updateDoctorByEmail(email, updatedDoctor);
        DoctorDocumentDto dto = DoctorDocumentDto.builder()
                .id(result.getId())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .clinicAddress(result.getClinicAddress())
                .experienceYears(result.getExperienceYears())
                .specializations(result.getSpecializations()
                        .stream().map(specializationMapper::toDto).toList())
                .build();
        messageProducer.sendDoctorToIndex(dto);
        return result;
    }

    @Override
    public Page<DoctorDocument> getAllDoctorsByParam(String params, Pageable pageable) {
        return elasticRepository.findByParams(params, pageable);
    }

    @Override
    @Transactional
    public void deleteDoctor(String jwt) {
        String email = jwtService.extractUsername(jwt);
        DoctorEntity doctor = cachingService.getDoctorByEmail(email);
        cachingService.deleteDoctorByEmail(email);
        repository.deleteById(doctor.getId());
        messageProducer.deleteDoctorFromIndex(doctor.getId());
    }
}
