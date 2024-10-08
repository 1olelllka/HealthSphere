package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PatientService {
    PatientEntity getPatient(String jwt);

    PatientEntity patchPatient(String accessToken, PatientEntity updatedPatientEntity);

    void deletePatient(String jwt);

    PatientEntity getPatientById(Long id);

    Page<PatientDocument> getAllPatients(String s, Pageable pageable);
}
