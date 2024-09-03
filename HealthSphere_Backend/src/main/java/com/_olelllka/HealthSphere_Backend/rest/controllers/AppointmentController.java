package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.AppointmentMapper;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api/v1")
public class AppointmentController {

    private AppointmentService service;
    private AppointmentMapper mapper;

    @Autowired
    public AppointmentController(AppointmentService service,
                                 AppointmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    @GetMapping("/patients/{userId}/appointments")
    public ResponseEntity<Page<AppointmentDto>> getAllAppointmentsForUser(@PathVariable Long userId,
                                                                    Pageable pageable) {
        Page<AppointmentEntity> entities =  service.getAllAppointmentsForPatient(userId, pageable);
        Page<AppointmentDto> result = entities.map(mapper::toDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @GetMapping("/doctors/{doctorId}/appointments")
    public ResponseEntity<Page<AppointmentDto>> getAllAppointmentsForDoctor(@PathVariable Long doctorId,
                                                                            Pageable pageable) {
        Page<AppointmentEntity> entities = service.getAllAppointmentsForDoctor(doctorId, pageable);
        Page<AppointmentDto> result = entities.map(mapper::toDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
