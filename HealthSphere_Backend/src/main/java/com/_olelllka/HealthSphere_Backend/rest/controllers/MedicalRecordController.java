package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.*;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class MedicalRecordController {

    private MedicalRecordService medicalRecordService;
    private MedicalRecordListMapper listMapper;
    private MedicalRecordDetailMapper detailMapper;
    private MedicalRecordDocumentMapper documentMapper;
    private PatientMapper patientMapper;
    private DoctorDetailMapper doctorMapper;

    @Autowired
    public MedicalRecordController(MedicalRecordService medicalRecordService,
                               MedicalRecordListMapper medicalRecordListMapper,
                                   MedicalRecordDetailMapper medicalRecordDetailMapper,
                                   MedicalRecordDocumentMapper documentMapper,
                                   PatientMapper patientMapper,
                                   DoctorDetailMapper doctorMapper) {
        this.listMapper = medicalRecordListMapper;
        this.medicalRecordService = medicalRecordService;
        this.detailMapper = medicalRecordDetailMapper;
        this.documentMapper = documentMapper;
        this.patientMapper = patientMapper;
        this.doctorMapper = doctorMapper;
    }

    @GetMapping("/patient/{id}/medical-records")
    public ResponseEntity<Page<MedicalRecordListDto>> getAllMedicalRecords(@PathVariable Long id,
                                                                           Pageable pageable,
                                                                           @RequestParam(name="diagnosis", required = false) Optional<String> diagnosis,
                                                                           @RequestParam(name = "from", required = false)
                                                                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
                                                                           @RequestParam(name = "to", required = false) Optional<LocalDate> to) {
        if (diagnosis.isEmpty() && from.isEmpty() && to.isEmpty()) {
            Page<MedicalRecordEntity> medicalRecordEntities = medicalRecordService.getAllRecordsByPatientId(id, pageable);
            Page<MedicalRecordListDto> result = medicalRecordEntities.map(listMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            Page<MedicalRecordDocument> medicalRecordDocuments = medicalRecordService.getRecordsByParams(id, diagnosis.orElse(null),
                    from.orElse(null), to.orElse(null), pageable);
            Page<MedicalRecordListDto> result = medicalRecordDocuments.map(documentMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("/patient/medical-records/{id}")
    public ResponseEntity<MedicalRecordDetailDto> getDetailedMedicalRecord(@PathVariable Long id) {
        MedicalRecordEntity medicalRecordEntity = medicalRecordService
                .getDetailedMedicalRecordForPatient(id);
        return new ResponseEntity<>(detailMapper.toDto(medicalRecordEntity), HttpStatus.OK);
    }

    @PostMapping("/patient/medical-records")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordDetailDto> createMedicalRecord(@RequestBody @Valid MedicalRecordDetailDto dto,
                                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        MedicalRecordEntity entity = MedicalRecordEntity.builder()
                        .patient(patientMapper.toEntity(dto.getPatient()))
                        .doctor(doctorMapper.toEntity(dto.getDoctor()))
                        .recordDate(dto.getRecordDate())
                        .treatment(dto.getTreatment())
                        .diagnosis(dto.getDiagnosis())
                        .prescription(dto.getPrescription())
                        .build();
        MedicalRecordEntity created = medicalRecordService.createMedicalRecord(entity);
        return new ResponseEntity<>(detailMapper.toDto(created), HttpStatus.CREATED);
    }

    @PatchMapping("/patient/medical-records/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordDetailDto> patchDetailedMedicalRecord(@PathVariable Long id,
                                                                             @RequestBody @Valid MedicalRecordDetailDto dto,
                                                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(message -> message.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        MedicalRecordEntity entity = detailMapper.toEntity(dto);
        MedicalRecordEntity updated = medicalRecordService.patchMedicalRecordForPatient(id, entity);
        return new ResponseEntity<>(detailMapper.toDto(updated), HttpStatus.OK);
    }

    @DeleteMapping("/patient/medical-records/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity deleteMedicalRecordById(@PathVariable Long id) {
        medicalRecordService.deleteById(id);
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }
}
