package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionDto;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionMedicineDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PrescriptionMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.PrescriptionMedicineMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
@Tag(name="Prescriptions", description = "Manage the prescriptions. Requires authorization")
public class PrescriptionsController {


    private PrescriptionMapper prescriptionMapper;
    private PrescriptionService service;
    private PrescriptionMedicineMapper medicineMapper;

    @Autowired
    public PrescriptionsController(PrescriptionMapper prescriptionMapper,
                                   PrescriptionService service,
                                   PrescriptionMedicineMapper medicineMapper) {
        this.prescriptionMapper = prescriptionMapper;
        this.service = service;
        this.medicineMapper = medicineMapper;
    }

    @Operation(summary = "Create a prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PrescriptionDto.class))
                    }
            ),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/prescriptions")
    public ResponseEntity<PrescriptionDto> createPrescription(@RequestBody @Valid PrescriptionDto dto,
                                                              @RequestHeader(name="Authorization") String header,
                                                              BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PrescriptionEntity entity = prescriptionMapper.toEntity(dto);
        PrescriptionEntity saved = service.createPrescription(entity, header.substring(7));
        return new ResponseEntity<>(prescriptionMapper.toDto(saved), HttpStatus.CREATED);
    }

    @Operation(summary = "Add medicine to specific prescription.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PrescriptionMedicineDto.class))
                    }
            ),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/prescriptions/{id}/medicine")
    public ResponseEntity<PrescriptionMedicineDto> addMedicineToPrescription(@PathVariable Long id,
                                                                             @RequestBody @Valid PrescriptionMedicineDto dto,
                                                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PrescriptionMedicineEntity entity = medicineMapper.toEntity(dto);
        PrescriptionMedicineEntity saved = service.addMedicineToPrescription(id, entity);
        return new ResponseEntity<>(medicineMapper.toDto(saved), HttpStatus.CREATED);
    }

    @Operation(summary = "Get list of medicine for specific prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PrescriptionMedicineDto.class))
                            )
                    }
            ),
            @ApiResponse(responseCode = "404", description = "Not Found Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping("/prescriptions/{id}/medicine")
    public ResponseEntity<List<PrescriptionMedicineDto>> showMedicineByPrescriptionId(@PathVariable Long id) {
        List<PrescriptionMedicineEntity> entities = service.getAllMedicineToPrescription(id);
        List<PrescriptionMedicineDto> result = entities.stream().map(medicineMapper::toDto).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Update the medicine in specific prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PrescriptionMedicineDto.class))
                    }
            ),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @PatchMapping("/prescriptions/medicine/{id}")
    public ResponseEntity<PrescriptionMedicineDto> updateTheMedicineById(@PathVariable Long id,
                                                                         @RequestBody @Valid PrescriptionMedicineDto dto,
                                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getAllErrors().stream().map(err -> err.getDefaultMessage()).collect(Collectors.joining(" "));
            throw new ValidationException(msg);
        }
        PrescriptionMedicineEntity entity = medicineMapper.toEntity(dto);
        PrescriptionMedicineEntity updated = service.updateTheMedicineById(id, entity);
        return new ResponseEntity<>(medicineMapper.toDto(updated), HttpStatus.OK);
    }

    @Operation(summary = "Delete a specific medicine")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PrescriptionMedicineDto.class))
                    }
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/prescriptions/medicine/{id}")
    public ResponseEntity removeMedicineFromPrescription(@PathVariable Long id) {
        service.removeMedicineById(id);
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Delete a specific prescription")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PrescriptionMedicineDto.class))
                    }
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/prescriptions/{id}")
    public ResponseEntity deletePrescriptionById(@PathVariable Long id) {
        service.deletePrescription(id);
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }
}
