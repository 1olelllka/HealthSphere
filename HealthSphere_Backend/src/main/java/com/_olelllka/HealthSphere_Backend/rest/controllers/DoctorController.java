package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDetailMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDocumentMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorListMapper;
import com._olelllka.HealthSphere_Backend.service.DoctorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/api/v1")
public class DoctorController {

    private static final Logger log = LoggerFactory.getLogger(DoctorController.class);
    private DoctorService doctorService;
    private DoctorListMapper listMapper;
    private DoctorDetailMapper detailMapper;
    private DoctorDocumentMapper documentMapper;

    @Autowired
    public DoctorController(DoctorService doctorService,
                            DoctorListMapper listMapper,
                            DoctorDetailMapper detailMapper,
                            DoctorDocumentMapper documentMapper) {
        this.doctorService = doctorService;
        this.listMapper = listMapper;
        this.detailMapper = detailMapper;
        this.documentMapper = documentMapper;
    }

    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorListDto>> getListOfDoctors(Pageable pageable,
                                                                @RequestParam(name="search", required = false) String params) {

        if (params == null || params.isEmpty()) {
            Page<DoctorEntity> doctors = doctorService.getAllDoctors(pageable);
            Page<DoctorListDto> result = doctors.map(listMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            Page<DoctorDocument> doctors = doctorService.getAllDoctorsByParam(params, pageable);
            Page<DoctorListDto> result = doctors.map(documentMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDetailDto> getDoctorById(@PathVariable Long id) {
        DoctorEntity doctor = doctorService.getDoctorById(id);
        return new ResponseEntity<>(detailMapper.toDto(doctor), HttpStatus.OK);
    }

    @GetMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDetailDto> getDoctorByMyEmail(@RequestHeader(name="Authorization") String header) {
        DoctorEntity doctor = doctorService.getDoctorByEmail(header.substring(7));
        return new ResponseEntity<>(detailMapper.toDto(doctor), HttpStatus.OK);
    }

    @PatchMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDetailDto> patchDoctorByMyEmail(@RequestBody DoctorDetailDto doctor,
                                                                @RequestHeader(name="Authorization") String header) {
        DoctorEntity doctorEntity = detailMapper.toEntity(doctor);
        DoctorEntity patchedDoctor = doctorService.patchDoctor(header.substring(7), doctorEntity);
        return new ResponseEntity<>(detailMapper.toDto(patchedDoctor), HttpStatus.OK);
    }

    @DeleteMapping("/doctors/{id}")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity deleteDoctorByMyEmail(@PathVariable Long id,
                                                HttpServletRequest request) throws ServletException {
        doctorService.deleteDoctorById(id);
        request.logout();
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
