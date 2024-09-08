package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class PatientController {

    private PatientService patientService;
    private PatientMapper mapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper mapper) {
        this.patientService = patientService;
        this.mapper = mapper;
    }


    @PreAuthorize("hasRole('PATIENT')")
    @GetMapping("/patient/me")
    public ResponseEntity<PatientDto> getPatientInfo(@RequestHeader(name="Authorization") String header) {
        PatientEntity patient = patientService.getPatient(header.substring(7));
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<PatientDto> getDetailedPatientInfoById(@PathVariable Long id) {
        PatientEntity patient = patientService.getPatientById(id);
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @PatchMapping("/patient/{id}")
    public ResponseEntity<PatientDto> patchPatient(@PathVariable Long id,
                                                   @Valid @RequestBody PatientDto updated,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PatientEntity updatedPatientEntity = mapper.toEntity(updated);
        PatientEntity patient = patientService.patchPatient(id, updatedPatientEntity);
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('PATIENT')")
    @DeleteMapping("/patient/{id}")
    public ResponseEntity deletePatient(@PathVariable Long id,
                                        HttpServletRequest request) throws Exception{
        patientService.deleteByEmail(id);
        request.logout();
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }
}