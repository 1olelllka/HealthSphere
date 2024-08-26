package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.dto.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.DoctorListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDetailMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorListMapper;
import com._olelllka.HealthSphere_Backend.service.DoctorService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
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

    private DoctorService doctorService;
    private DoctorListMapper listMapper;
    private DoctorDetailMapper detailMapper;

    @Autowired
    public DoctorController(DoctorService doctorService,
                            DoctorListMapper listMapper,
                            DoctorDetailMapper detailMapper) {
        this.doctorService = doctorService;
        this.listMapper = listMapper;
        this.detailMapper = detailMapper;
    }

    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorListDto>> getListOfDoctors(Pageable pageable) {
        Page<DoctorEntity> doctors = doctorService.getAllDoctors(pageable);
        Page<DoctorListDto> result = doctors.map(listMapper::toDto);
        return new ResponseEntity<>(result, HttpStatus.OK);
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

    @DeleteMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity deleteDoctorByMyEmail(@RequestHeader(name="Authorization") String header,
                                                HttpServletRequest request) throws ServletException {
        doctorService.deleteDoctorByEmail(header.substring(7));
        request.logout();
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
