package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;

import java.util.List;

public interface MedicalRecordService {
    List<MedicalRecordEntity> getAllRecordsForPatient(String patientJwt);
}
