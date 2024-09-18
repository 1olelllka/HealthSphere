package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordRepository;
import com._olelllka.HealthSphere_Backend.repositories.PrescriptionRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.AccessDeniedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotAuthorizedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.MedicalRecordMessageProducer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;

@Service
@Log
public class MedicalRecordServiceImpl implements MedicalRecordService {


    private MedicalRecordRepository medicalRecordRepository;
    private MedicalRecordElasticRepository elasticRepository;
    private MedicalRecordMessageProducer messageProducer;
    private DoctorRepository doctorRepository;
    private PrescriptionRepository prescriptionRepository;
    private JwtService jwtService;

    @Autowired
    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    MedicalRecordElasticRepository elasticRepository,
                                    MedicalRecordMessageProducer messageProducer,
                                    DoctorRepository doctorRepository,
                                    PrescriptionRepository prescriptionRepository,
                                    JwtService jwtService) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.elasticRepository = elasticRepository;
        this.messageProducer = messageProducer;
        this.doctorRepository = doctorRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.jwtService = jwtService;
    }

    @Override
    public Page<MedicalRecordEntity> getAllRecordsByPatientId(Long id, Pageable pageable) {
        return medicalRecordRepository.findByPatientId(id, pageable);
    }

    @Override
    public MedicalRecordEntity getDetailedMedicalRecordForPatient(Long id) {
        return medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("Medical Record with such id was not found."));
    }

    @Override
    public Page<MedicalRecordDocument> getRecordsByParams(Long id, String diagnosis, String from, String to, Pageable pageable) {
        return elasticRepository.findByParams(id, diagnosis, from, to, pageable);
    }

    @Override
    @Transactional
    public MedicalRecordEntity patchMedicalRecordForPatient(Long id, MedicalRecordEntity updated, String jwt) {
        return medicalRecordRepository.findById(id).map(record -> {
            String email = jwtService.extractUsername(jwt);
            log.info(email);
            if (!Objects.equals(record.getDoctor().getUser().getEmail(), email)) {
                log.info(record.getDoctor().getUser().getEmail());
                throw new AccessDeniedException("You are not allowed to perform this action");
            }
            Optional.ofNullable(updated.getDiagnosis()).ifPresent(record::setDiagnosis);
            Optional.ofNullable(updated.getTreatment()).ifPresent(record::setTreatment);
            Optional.ofNullable(updated.getPrescription()).ifPresent(record::setPrescription);
            MedicalRecordEntity result =  medicalRecordRepository.save(record);
            MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder()
                            .id(result.getId())
                            .diagnosis(result.getDiagnosis())
                    .user_id(result.getPatient().getId())
                    .recordDate(result.getRecordDate())
                    .doctor(result.getDoctor().getFirstName() + " " + result.getDoctor().getLastName())
                            .treatment(result.getTreatment()).build();
            messageProducer.sendMedicalRecordCreateUpdate(dto);
            return result;
        }).orElseThrow(() -> new NotFoundException("Medical Record with such id was not found."));
    }

    @Override
    @Transactional
    public MedicalRecordEntity createMedicalRecord(MedicalRecordEntity entity, String jwt) {
        String email = jwtService.extractUsername(jwt);
        DoctorEntity doctor = doctorRepository.findByUserEmail(email).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
        entity.setDoctor(doctor);
        MedicalRecordEntity result = medicalRecordRepository.save(entity);
        MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder()
                .id(result.getId())
                .recordDate(result.getRecordDate())
                .user_id(result.getPatient().getId())
                .diagnosis(result.getDiagnosis())
                .treatment(result.getTreatment())
                .doctor(result.getDoctor().getFirstName() + " " + result.getDoctor().getLastName())
                .build();
        messageProducer.sendMedicalRecordCreateUpdate(dto);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id, String jwt) {
        MedicalRecordEntity entity = medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("Medical record with such id was not found."));
        String email = jwtService.extractUsername(jwt);
        if (!Objects.equals(email, entity.getDoctor().getUser().getEmail())) {
            throw new AccessDeniedException("You are not allowed to perform this action.");
        }
        if (entity.getPrescription() != null) {
            prescriptionRepository.deleteById(entity.getPrescription().getId());
        }
        medicalRecordRepository.deleteById(id);
        messageProducer.sendMedicalRecordDelete(id);
    }
}
