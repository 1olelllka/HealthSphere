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

import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {

    private DoctorRepository repository;
    private DoctorElasticRepository elasticRepository;
    private JwtService jwtService;
    private DoctorMessageProducer messageProducer;
    private SpecializationMapper specializationMapper;

    @Autowired
    public DoctorServiceImpl(DoctorRepository repository,
                             JwtService jwtService,
                             DoctorMessageProducer messageProducer,
                             DoctorElasticRepository elasticRepository,
                             SpecializationMapper specializationMapper) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.messageProducer = messageProducer;
        this.elasticRepository = elasticRepository;
        this.specializationMapper = specializationMapper;
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

    @Transactional
    @Override
    public DoctorEntity patchDoctor(Long id, DoctorEntity updatedDoctor) {
        return repository.findById(id).map(doctor -> {
            Optional.ofNullable(updatedDoctor.getFirstName()).ifPresent(doctor::setFirstName);
            Optional.ofNullable(updatedDoctor.getLastName()).ifPresent(doctor::setLastName);
            Optional.ofNullable(updatedDoctor.getExperienceYears()).ifPresent(doctor::setExperienceYears);
            Optional.ofNullable(updatedDoctor.getPhoneNumber()).ifPresent(doctor::setPhoneNumber);
            Optional.ofNullable(updatedDoctor.getClinicAddress()).ifPresent(doctor::setClinicAddress);
            Optional.ofNullable(updatedDoctor.getLicenseNumber()).ifPresent(doctor::setLicenseNumber);
            Optional.ofNullable(updatedDoctor.getSpecializations()).ifPresent(doctor::setSpecializations);
            DoctorEntity result = repository.save(doctor);
            DoctorDocumentDto dto = DoctorDocumentDto.builder()
                            .id(result.getId())
                            .firstName(result.getFirstName())
                            .lastName(result.getLastName())
                            .clinicAddress(doctor.getClinicAddress())
                            .experienceYears(doctor.getExperienceYears())
                    .specializations(doctor.getSpecializations()
                            .stream().map(specializationMapper::toDto).toList())
                            .build();
            messageProducer.sendDoctorToIndex(dto);
            return result;
        }).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
    }

    @Override
    public Page<DoctorDocument> getAllDoctorsByParam(String params, Pageable pageable) {
        return elasticRepository.findByParams(params, pageable);
    }

    @Override
    @Transactional
    public void deleteDoctorById(Long id) {
        repository.deleteById(id);
        messageProducer.deleteDoctorFromIndex(id);
    }
}
