package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
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


    @GetMapping("/patient/me")
    public ResponseEntity<PatientDto> getPatientInfo(@RequestHeader(name="Authorization") String header) {
        PatientEntity patient = patientService.getPatient(header.substring(7));
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }

    @PatchMapping("/patient/me")
    public ResponseEntity<PatientDto> patchPatient(@RequestHeader(name="Authorization") String header,
                                                   @Valid @RequestBody PatientDto updated,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PatientEntity updatedPatientEntity = mapper.toEntity(updated);
        PatientEntity patient = patientService.patchPatient(header.substring(7), updatedPatientEntity);
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }
}
