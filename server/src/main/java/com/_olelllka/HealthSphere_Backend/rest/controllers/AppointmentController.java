package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.appointments.UpdateAppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import com._olelllka.HealthSphere_Backend.mapper.impl.AppointmentMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
public class AppointmentController {

    private static final Logger log = LoggerFactory.getLogger(AppointmentController.class);
    private AppointmentService service;
    private AppointmentMapper mapper;

    @Autowired
    public AppointmentController(AppointmentService service,
                                 AppointmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    @GetMapping("/patients/{patientId}/appointments")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsForPatient(@PathVariable Long patientId) {
        List<AppointmentEntity> entities =  service.getAllAppointmentsForPatient(patientId);
        List<AppointmentDto> result = entities.stream().map(mapper::toDto).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/doctors/{doctorId}/appointments")
    public ResponseEntity<List<AppointmentDto>> getAllAppointmentsForDoctor(@PathVariable Long doctorId) {
        List<AppointmentEntity> entities = service.getAllAppointmentsForDoctor(doctorId);
        List<AppointmentDto> result = entities.stream().map(mapper::toDto).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/patients/appointments/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        AppointmentEntity appointment = service.getAppointmentById(id);
        return new ResponseEntity<>(mapper.toDto(appointment), HttpStatus.OK);
    }

    @PostMapping("/patients/appointments")
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody AppointmentDto dto,
                                                            BindingResult bindingResult,
                                                             @RequestHeader(name="Authorization") String header,
                                                             HttpServletRequest request) {
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

    @PatchMapping("/patients/appointments/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@RequestBody @Valid UpdateAppointmentDto dto,
                                                            @PathVariable Long id,
                                                            HttpServletRequest request,
                                                            @RequestHeader(name = "Authorization") String header,
                                                            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        if (dto.getStatus() != null && dto.getStatus() == Status.CANCELED) {
            service.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.ACCEPTED);
        } else if (dto.getAppointmentDate() != null) {
            AppointmentEntity getAppointment = service.getAppointmentById(id);
            service.deleteById(id);
            AppointmentEntity newAppointment = AppointmentEntity.builder()
                    .reason(dto.getReason())
                    .doctor(getAppointment.getDoctor())
                    .patient(getAppointment.getPatient())
                    .appointmentDate(dto.getAppointmentDate())
                    .build();
            if (request.isUserInRole("PATIENT")) {
                AppointmentEntity result = service.createNewAppointmentForPatient(newAppointment, header.substring(7));
                return new ResponseEntity<>(mapper.toDto(result), HttpStatus.CREATED);
            } else if (request.isUserInRole("DOCTOR")) {
                AppointmentEntity result = service.createNewAppointmentForDoctor(newAppointment, header.substring(7));
                return new ResponseEntity<>(mapper.toDto(result), HttpStatus.CREATED);
            }
        }
        AppointmentEntity entity = AppointmentEntity.builder()
                .reason(dto.getReason())
                .status(dto.getStatus())
                .build();
        AppointmentEntity updated = service.updateEntity(entity, id);
        return new ResponseEntity<>(mapper.toDto(updated), HttpStatus.OK);
    }
}
