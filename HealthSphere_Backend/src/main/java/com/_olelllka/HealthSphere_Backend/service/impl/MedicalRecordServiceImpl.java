package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorRecordList;
import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.MedicalRecordMessageProducer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Log
public class MedicalRecordServiceImpl implements MedicalRecordService {


    private MedicalRecordRepository medicalRecordRepository;
    private MedicalRecordElasticRepository elasticRepository;
    private MedicalRecordMessageProducer messageProducer;

    @Autowired
    public MedicalRecordServiceImpl(MedicalRecordRepository medicalRecordRepository,
                                    MedicalRecordElasticRepository elasticRepository,
                                    MedicalRecordMessageProducer messageProducer) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.elasticRepository = elasticRepository;
        this.messageProducer = messageProducer;
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
    public Page<MedicalRecordDocument> getRecordsByParams(Long id, String diagnosis, LocalDate from, LocalDate to, Pageable pageable) {
        String localFrom = from.toString();
        String localTo = to.toString();
        return elasticRepository.findByParams(id, diagnosis, localFrom, localTo, pageable);
    }

    @Override
    public MedicalRecordEntity patchMedicalRecordForPatient(Long id, MedicalRecordEntity updated) {
        return medicalRecordRepository.findById(id).map(record -> {
            Optional.ofNullable(updated.getDiagnosis()).ifPresent(record::setDiagnosis);
            Optional.ofNullable(updated.getTreatment()).ifPresent(record::setTreatment);
            Optional.ofNullable(updated.getPrescription()).ifPresent(record::setPrescription);
            MedicalRecordEntity result =  medicalRecordRepository.save(record);
            MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder()
                            .id(result.getId())
                            .diagnosis(result.getDiagnosis())
                            .treatment(result.getTreatment()).build();
            messageProducer.sendMedicalRecordCreateUpdate(dto);
            return result;
        }).orElseThrow(() -> new NotFoundException("Medical Record with such id was not found."));
    }

    @Override
    @Transactional
    public MedicalRecordEntity createMedicalRecord(MedicalRecordEntity entity) {
        MedicalRecordEntity result = medicalRecordRepository.save(entity);
        MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder()
                .id(result.getId())
                .recordDate(result.getRecordDate())
                .user_id(result.getPatient().getId())
                .diagnosis(result.getDiagnosis())
                .treatment(result.getTreatment())
                .doctor(DoctorRecordList.builder()
                        .id(result.getDoctor().getId())
                        .firstName(result.getDoctor().getFirstName())
                        .lastName(result.getDoctor().getLastName()).build())
                .build();
        messageProducer.sendMedicalRecordCreateUpdate(dto);
        return result;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        medicalRecordRepository.deleteById(id);
        messageProducer.sendMedicalRecordDelete(id);
    }
}
