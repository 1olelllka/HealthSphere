package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DoctorService {
    Page<DoctorEntity> getAllDoctors(Pageable pageable);

    DoctorEntity getDoctorByEmail(String jwt);

    DoctorEntity getDoctorById(Long id);

    DoctorEntity patchDoctor(String jwt, DoctorEntity doctor);

    Page<DoctorDocument> getAllDoctorsByParam(String params, Pageable pageable);

    void deleteDoctor(String jwt);
}
