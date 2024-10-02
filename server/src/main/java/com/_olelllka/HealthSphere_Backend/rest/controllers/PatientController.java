package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientListMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1")
@Tag(name="Patients", description = "Search for patients and their details.")
public class PatientController {

    private PatientService patientService;
    private PatientMapper mapper;
    private PatientListMapper listMapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper mapper, PatientListMapper listMapper) {
        this.patientService = patientService;
        this.mapper = mapper;
        this.listMapper = listMapper;
    }

    @Operation(summary = "Get all of the patients (available only for doctors)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all of the patients",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Page.class)
                    )),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)
                    ))
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/patients")
    public ResponseEntity<Page<PatientListDto>> getAllOfThePatients(@RequestParam(required = false, name = "search") String params,
                                                                    @ParameterObject Pageable pageable) {
        Page<PatientDocument> documents = patientService.getAllPatients(params == null ? "" : params, pageable);
        Page<PatientListDto> patients = documents.map(listMapper::toDto);
        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    @Operation(summary = "Get info about specific patient (for doctors only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PatientDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Patient with such id was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/patients/{id}")
    public ResponseEntity<PatientDto> getDetailedPatientInfoById(@PathVariable Long id) {
        PatientEntity patient = patientService.getPatientById(id);
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }
}
