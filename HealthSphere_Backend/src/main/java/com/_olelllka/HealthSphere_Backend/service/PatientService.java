package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;

public interface PatientService {
    PatientEntity getPatient(String jwt);

    PatientEntity patchPatient(Long accessToken, PatientEntity updatedPatientEntity);

    void deleteByEmail(Long id);

    PatientEntity getPatientById(Long id);
}
