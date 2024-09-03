package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.AppointmentMapper;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

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


    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<Page<AppointmentDto>> getAllAppointmentsForPatient(@PathVariable Long patientId,
                                                                             Pageable pageable) {
        Page<AppointmentEntity> entities =  service.getAllAppointmentsForPatient(patientId, pageable);
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

    @GetMapping("/patients/appointments/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        AppointmentEntity appointment = service.getAppointmentById(id);
        return new ResponseEntity<>(mapper.toDto(appointment), HttpStatus.OK);
    }

    @PostMapping("/patients/appointments")
    public ResponseEntity<AppointmentDto> createAppointment(@RequestBody @Valid AppointmentDto dto,
                                                            HttpServletRequest request,
                                                            @RequestHeader(name="Authorization") String header,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        AppointmentEntity entity = mapper.toEntity(dto);
        AppointmentEntity created = new AppointmentEntity();
        if (request.isUserInRole("PATIENT")) {
            created = service.createNewAppointmentForPatient(entity, header.substring(7));
        } else if (request.isUserInRole("DOCTOR")) {
            created = service.createNewAppointmentForDoctor(entity, header.substring(7));
        }
        return new ResponseEntity<>(mapper.toDto(created), HttpStatus.CREATED);
    }

    @DeleteMapping("/patients/appointments/{id}")
    public ResponseEntity deleteAppointmentById(@PathVariable Long id) {
        service.deleteById(id);
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }
}
