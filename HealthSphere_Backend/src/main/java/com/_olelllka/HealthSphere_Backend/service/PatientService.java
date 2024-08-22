package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;

public interface PatientService {
    PatientEntity getPatient(String jwt);
}
