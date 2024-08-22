package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotAuthorizedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.PatientService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping(path="/api/v1")
@Log
public class PatientController {

    private PatientService patientService;
    private PatientMapper mapper;

    @Autowired
    public PatientController(PatientService patientService, PatientMapper mapper) {
        this.patientService = patientService;
        this.mapper = mapper;
    }


    @GetMapping("/patient/me")
    public ResponseEntity<PatientDto> getPatientInfo(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new NotAuthorizedException("Log in to Proceed");
        }
        Cookie accessToken = Arrays.stream(cookies)
                .filter(cookie -> "accessToken".equals(cookie.getName())).findFirst()
                .orElseThrow(() -> new NotAuthorizedException("Log in to proceed.")); // Basically it's impossible to have such an error because of Spring Security but just in case.
        PatientEntity patient = patientService.getPatient(accessToken.getValue());
        log.info("" + mapper.toDto(patient));
        return new ResponseEntity<>(mapper.toDto(patient), HttpStatus.OK);
    }
}
