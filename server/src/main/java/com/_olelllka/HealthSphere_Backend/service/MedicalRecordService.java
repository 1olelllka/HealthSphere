package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface MedicalRecordService {
    Page<MedicalRecordEntity> getAllRecordsByPatientId(Long id, Pageable pageable);

    MedicalRecordEntity getDetailedMedicalRecordForPatient(Long id);

    Page<MedicalRecordDocument> getRecordsByParams(Long id, String diagnosis, String from, String to, Pageable pageable);

    MedicalRecordEntity patchMedicalRecordForPatient(Long id, MedicalRecordEntity dto);

    MedicalRecordEntity createMedicalRecord(MedicalRecordEntity entity, String jwt);

    void deleteById(Long id);
}
