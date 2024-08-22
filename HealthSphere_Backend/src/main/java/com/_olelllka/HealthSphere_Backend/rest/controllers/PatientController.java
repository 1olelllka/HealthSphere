package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotAuthorizedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);
    private PatientService patientService;
    private PatientMapper mapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper mapper) {
        this.patientService = patientService;
        this.mapper = mapper;
    }


    @GetMapping("/patient/me")
    public ResponseEntity<PatientDto> getPatientInfo(HttpServletRequest request) {
        Cookie accessToken = getAccessTokenCookie(request);
        PatientEntity patient = patientService.getPatient(accessToken.getValue());
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }

    @PatchMapping("/patient/me")
    public ResponseEntity<PatientDto> patchPatient(HttpServletRequest request,
                                                   @Valid @RequestBody PatientDto updated,
                                                   BindingResult bindingResult) {
        Cookie accessToken = getAccessTokenCookie(request);
        log.info("" + bindingResult);
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PatientEntity updatedPatientEntity = mapper.toEntity(updated);
        PatientEntity patient = patientService.patchPatient(accessToken.getValue(), updatedPatientEntity);
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }

    private Cookie getAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new NotAuthorizedException("Log in to Proceed");
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName())).findFirst()
                .orElseThrow(() -> new NotAuthorizedException("Log in to proceed."));
    }
}
