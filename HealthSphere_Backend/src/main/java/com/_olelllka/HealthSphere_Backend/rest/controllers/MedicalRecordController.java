package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.MedicalRecordDetailMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.MedicalRecordListMapper;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api/v1")
public class MedicalRecordController {

    private MedicalRecordService medicalRecordService;
    private MedicalRecordListMapper listMapper;
    private MedicalRecordDetailMapper detailMapper;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService,
                               MedicalRecordListMapper medicalRecordListMapper,
                                   MedicalRecordDetailMapper medicalRecordDetailMapper) {
        this.listMapper = medicalRecordListMapper;
        this.medicalRecordService = medicalRecordService;
        this.detailMapper = medicalRecordDetailMapper;
    }

    @GetMapping("/patient/medical-records")
    public ResponseEntity<List<MedicalRecordListDto>> getAllMedicalRecords(@RequestHeader(name="Authorization") String header) {
        List<MedicalRecordEntity> medicalRecordEntities = medicalRecordService.getAllRecordsForPatient(header.substring(7));
        return new ResponseEntity<>(medicalRecordEntities.stream().map(entity -> listMapper.toDto(entity)).toList(), HttpStatus.OK);
    }

    @GetMapping("/patient/medical-records/{id}")
    public ResponseEntity<MedicalRecordDetailDto> getDetailedMedicalRecord(@PathVariable Long id) {
        MedicalRecordEntity medicalRecordEntity = medicalRecordService
                .getDetailedMedicalRecordForPatient(id);
        return new ResponseEntity<>(detailMapper.toDto(medicalRecordEntity), HttpStatus.OK);
    }
}
