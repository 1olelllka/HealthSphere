package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.*;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
@Tag(name="Medical Records", description = "Search and manage medical records from patient's and doctor's sides. Requires authorization.")
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
                                                                           @ParameterObject Pageable pageable,
                                                                           @RequestParam(name="diagnosis", required = false) String diagnosis,
                                                                           @RequestParam(name = "from", required = false)
                                                                               String from,
                                                                           @RequestParam(name = "to", required = false) String to) {
        if (diagnosis == null && from == null && to == null) {
            Page<MedicalRecordEntity> medicalRecordEntities = medicalRecordService.getAllRecordsByPatientId(id, pageable);
            Page<MedicalRecordListDto> result = medicalRecordEntities.map(listMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            Page<MedicalRecordDocument> medicalRecordDocuments = medicalRecordService.getRecordsByParams(id, !diagnosis.isEmpty() ? diagnosis : "",
                    !from.isEmpty() ? from : ("1970-01-01"), !to.isEmpty() ? to : (LocalDate.now().toString()), pageable);
            Page<MedicalRecordListDto> result = medicalRecordDocuments.map(documentMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get detailed medical record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MedicalRecordDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping("/patient/medical-records/{id}")
    public ResponseEntity<MedicalRecordDetailDto> getDetailedMedicalRecord(@PathVariable Long id) {
        MedicalRecordEntity medicalRecordEntity = medicalRecordService
                .getDetailedMedicalRecordForPatient(id);
        MedicalRecordDetailDto result = detailMapper.toDto(medicalRecordEntity);
        result.setPatient(patientMapper.toDto(medicalRecordEntity.getPatient()));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Create medical record (only for doctors).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MedicalRecordDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PostMapping("/patient/medical-records")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordDetailDto> createMedicalRecord(@RequestHeader(name="Authorization") String header,
                                                                      @RequestBody @Valid MedicalRecordDetailDto dto,
                                                                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        MedicalRecordEntity entity = MedicalRecordEntity.builder()
                        .patient(patientMapper.toEntity(dto.getPatient()))
                        .recordDate(dto.getRecordDate())
                        .treatment(dto.getTreatment())
                        .diagnosis(dto.getDiagnosis())
                        .prescription(dto.getPrescription())
                        .build();
        MedicalRecordEntity created = medicalRecordService.createMedicalRecord(entity, header.substring(7));
        return new ResponseEntity<>(detailMapper.toDto(created), HttpStatus.CREATED);
    }

    @Operation(summary = "Update medical record (only for doctors).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MedicalRecordDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PatchMapping("/patient/medical-records/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalRecordDetailDto> patchDetailedMedicalRecord(@PathVariable Long id,
                                                                             @RequestHeader(name="Authorization") String header,
                                                                             @RequestBody @Valid MedicalRecordDetailDto dto,
                                                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(message -> message.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        MedicalRecordEntity entity = detailMapper.toEntity(dto);
        MedicalRecordEntity updated = medicalRecordService.patchMedicalRecordForPatient(id, entity, header.substring(7));
        return new ResponseEntity<>(detailMapper.toDto(updated), HttpStatus.OK);
    }

    @Operation(summary = "Delete medical record (only for doctors).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Not Found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @DeleteMapping("/patient/medical-records/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity deleteMedicalRecordById(@PathVariable Long id,
                                                  @RequestHeader(name="Authorization") String header) {
        medicalRecordService.deleteById(id, header.substring(7));
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }
}
