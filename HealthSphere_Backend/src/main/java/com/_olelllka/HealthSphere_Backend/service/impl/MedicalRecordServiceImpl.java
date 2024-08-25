package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicalRecordServiceImpl implements MedicalRecordService {

    private JwtService jwtService;
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    public MedicalRecordServiceImpl(JwtService jwtService,
                                    MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.jwtService = jwtService;
    }

    @Override
    public List<MedicalRecordEntity> getAllRecordsForPatient(String patientJwt) {
        String email = jwtService.extractUsername(patientJwt);
        return medicalRecordRepository.findByPatientEmail(email);
    }

    @Override
    public MedicalRecordEntity getDetailedMedicalRecordForPatient(Long id) {
        return medicalRecordRepository.findById(id).orElseThrow(() -> new NotFoundException("Medical Record with such id was not found."));
    }
}
