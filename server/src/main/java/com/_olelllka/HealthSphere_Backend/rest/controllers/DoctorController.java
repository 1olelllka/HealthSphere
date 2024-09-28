package com._olelllka.HealthSphere_Backend.rest.controllers;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.ErrorMessage;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorListDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDetailMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDocumentMapper;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorListMapper;
import com._olelllka.HealthSphere_Backend.service.DoctorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path="/api/v1")
@Tag(name="Doctors", description = "Search for doctors and manage doctor's account. Doctor accounts require authorization")
public class DoctorController {

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
    public ResponseEntity<Page<DoctorListDto>> getListOfDoctors(@ParameterObject Pageable pageable,
                                                                @RequestParam(name="search", required = false) String params) {

        if (params == null || params.isEmpty()) {
            Page<DoctorEntity> doctors = doctorService.getAllDoctors(pageable);
            Page<DoctorListDto> result = doctors.map(listMapper::toDto);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } else {
            Page<DoctorDocument> doctors = doctorService.getAllDoctorsByParam(params, pageable);
            Page<DoctorListDto> result = doctors.map(documentMapper::toDto);
            result.getContent().forEach(doctor -> {
                List<SpecializationDto> specializations = doctors.getContent().stream()
                        .filter(doc -> doc.getId().equals(doctor.getId()))
                        .flatMap(doc -> doc.getSpecializations().stream())
                        .map(name -> SpecializationDto.builder()
                                .specializationName(name)
                                .build())
                        .collect(Collectors.toList());
                doctor.setSpecializations(specializations);
            });
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @Operation(summary = "Get details about specific doctor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got details about doctor",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor with such id was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorDetailDto> getDoctorById(@PathVariable Long id) {
        DoctorEntity doctor = doctorService.getDoctorById(id);
        return new ResponseEntity<>(detailMapper.toDto(doctor), HttpStatus.OK);
    }

    @Operation(summary = "Get details about doctor profile (me).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Got details about profile",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @GetMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDetailDto> getDoctorByMyEmail(@RequestHeader(name="Authorization") String header) {
        DoctorEntity doctor = doctorService.getDoctorByEmail(header.substring(7));
        return new ResponseEntity<>(detailMapper.toDto(doctor), HttpStatus.OK);
    }

    @Operation(summary = "Update doctor's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DoctorDetailDto.class)
                    )}
            ),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "400", description = "Validation Error",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @PatchMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<DoctorDetailDto> patchDoctorByMyEmail(@RequestBody DoctorDetailDto doctor,
                                                                @RequestHeader(name="Authorization") String header) {
        DoctorEntity doctorEntity = detailMapper.toEntity(doctor);
        DoctorEntity patchedDoctor = doctorService.patchDoctor(header.substring(7), doctorEntity);
        return new ResponseEntity<>(detailMapper.toDto(patchedDoctor), HttpStatus.OK);
    }

    @Operation(summary = "Delete doctor's profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access Denied",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(responseCode = "404", description = "Doctor was not found.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
    })
    @DeleteMapping("/doctors/me")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity deleteDoctorByMyEmail(@RequestHeader(name="Authorization") String header,
                                                HttpServletRequest request) throws ServletException {
        doctorService.deleteDoctor(header.substring(7));
        request.logout();
        return new ResponseEntity(HttpStatus.ACCEPTED);
    }
}
