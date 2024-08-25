package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.MedicalRecordMapper;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
public class MedicalRecordController {

    private MedicalRecordService medicalRecordService;
    private MedicalRecordMapper medicalRecordMapper;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService,
                               MedicalRecordMapper medicalRecordMapper) {
        this.medicalRecordMapper = medicalRecordMapper;
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/patient/medical-records")
    public ResponseEntity<List<MedicalRecordListDto>> getAllMedicalRecords(@RequestHeader(name="Authorization") String header) {
        List<MedicalRecordEntity> medicalRecordEntities = medicalRecordService.getAllRecordsForPatient(header.substring(7));
        return new ResponseEntity<>(medicalRecordEntities.stream().map(entity -> medicalRecordMapper.toDto(entity)).toList(), HttpStatus.OK);
    }
}
