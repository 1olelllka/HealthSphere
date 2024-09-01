package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionDto;
import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionMedicineDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PrescriptionMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.PrescriptionMedicineMapper;
import com._olelllka.HealthSphere_Backend.rest.exceptions.ValidationException;
import com._olelllka.HealthSphere_Backend.service.PrescriptionService;
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

    @PreAuthorize("hasRole('DOCTOR')")
    @PostMapping("/prescriptions")
    public ResponseEntity<PrescriptionDto> createPrescription(@RequestBody @Valid  PrescriptionDto dto,
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

    @GetMapping("/prescriptions/{id}/medicine")
    public ResponseEntity<List<PrescriptionMedicineDto>> showMedicineByPrescriptionId(@PathVariable Long id) {
        List<PrescriptionMedicineEntity> entities = service.getAllMedicineToPrescription(id);
        List<PrescriptionMedicineDto> result = entities.stream().map(medicineMapper::toDto).toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('DOCTOR')")
    @DeleteMapping("/prescriptions/medicine/{id}")
    public ResponseEntity removeMedicineFromPrescription(@PathVariable Long id) {
        service.removeMedicineById(id);
        return new ResponseEntity(null, HttpStatus.ACCEPTED);
    }

}
