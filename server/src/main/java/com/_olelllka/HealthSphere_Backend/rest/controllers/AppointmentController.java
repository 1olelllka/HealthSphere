package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.appointments.UpdateAppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import com._olelllka.HealthSphere_Backend.mapper.impl.AppointmentMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
@Tag(name="Appointments", description = "Manage appointments for doctor and patient. Requires authorization.")
public class AppointmentController {

    private AppointmentService service;
    private AppointmentMapper mapper;

    @Autowired
    public AppointmentController(AppointmentService service,
                                 AppointmentMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }


    @Operation(summary = "Get all appointments for specific patient.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all appointments patient has",
            content = {@Content(mediaType = "application/json",
                    array =
                        @ArraySchema(schema =
                            @Schema(implementation = AppointmentDto.class)
                        )
            )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
            content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/appointments/patients/{patientId}")
    public ResponseEntity<?> getAllAppointmentsForPatient(@PageableDefault(sort = {"appointmentDate", "id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                                             @PathVariable Long patientId,
                                                                             @RequestParam(required = false) LocalDateTime from,
                                                                             @RequestParam(required = false) LocalDateTime to) {
        if (from == null || to == null) {
            Page<AppointmentEntity> entities =  service.getAllAppointmentsForPatient(patientId, pageable);
            Page<AppointmentDto> result = entities.map(mapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            List<AppointmentEntity> entities = service.getAllAppointmentsForPatientByParams(patientId, from, to);
            List<AppointmentDto> result = entities.stream().map(mapper::toDto).toList();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get all appointments for specific doctor.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got all appointments doctor has",
                    content = {@Content(mediaType = "application/json",
                            array =
                            @ArraySchema(schema =
                            @Schema(implementation = AppointmentDto.class)
                            )
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/appointments/doctors/{doctorId}")
    public ResponseEntity<?> getAllAppointmentsForDoctor(@PageableDefault(sort = {"appointmentDate", "id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                         @PathVariable Long doctorId,
                                                         @RequestParam(required = false) LocalDateTime from,
                                                         @RequestParam(required = false) LocalDateTime to) {
        if (from == null || to == null) {
        Page<AppointmentEntity> entities = service.getAllAppointmentsForDoctor(doctorId, pageable);
        Page<AppointmentDto> result = entities.map(mapper::toDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            List<AppointmentEntity> entities = service.getAllAppointmentsForDoctorByParams(doctorId, from, to);
            List<AppointmentDto> result = entities.stream().map(mapper::toDto).toList();
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get details about specific appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got details about appointment",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Appointment with such id was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable Long id) {
        AppointmentEntity appointment = service.getAppointmentById(id);
        return new ResponseEntity<>(mapper.toDto(appointment), HttpStatus.OK);
    }

    @Operation(summary = "Create an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Errors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Patient or Doctor with id specified in json was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "409", description = "Duplicate appointment date and patient or doctor",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/appointments")
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

    @Operation(summary = "Update an appointment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "201", description = "Rescheduled appointment successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "202", description = "Canceled appointment"),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Errors",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Patient or Doctor with id specified in json was not found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PatchMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDto> updateAppointment(@PathVariable Long id,
                                                            HttpServletRequest request,
                                                            @RequestHeader(name = "Authorization") String header,
                                                            @RequestBody @Valid UpdateAppointmentDto dto,
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
