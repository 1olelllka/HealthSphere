package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.DoctorServiceImpl;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.DoctorMessageProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class DoctorServiceUnitTest {

    @Mock
    private DoctorRepository repository;
    @Mock
    private JwtService jwtService;
    @Mock
    private DoctorMessageProducer messageProducer;
    @Mock
    private DoctorElasticRepository elasticRepository;
    @Mock
    private SpecializationMapper mapper;
    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    public void testThatGetAllDoctorsReturnPage() {
        // given
        Pageable pageable = PageRequest.of(1, 1);
        DoctorEntity entity = DoctorEntity.builder().firstName("First Name").build();
        Page<DoctorEntity> expected = new PageImpl<>(List.of(entity));
        // when
        when(repository.findAll(pageable)).thenReturn(expected);
        Page<DoctorEntity> result = doctorService.getAllDoctors(pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected)
        );
        verify(repository, times(1)).findAll(pageable);
    }

    @Test
    public void testThatGetDoctorByIdThrowsNotFoundException() {
        // given
        Long id = 1L;
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorById(id));
    }

    @Test
    public void testThatGetDoctorByEmailThrowsException() {
        // given
        String jwt = "jwt";
        String email = "email";
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(repository.findByUserEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> doctorService.getDoctorByEmail(jwt));
    }

    @Test
    public void testThatGetDoctorByIdReturnsDoctor() {
        // given
        Long id = 1L;
        DoctorEntity doctor = DoctorEntity.builder().id(1L).firstName("First Name").build();
        // when
        when(repository.findById(id)).thenReturn(Optional.of(doctor));
        DoctorEntity result = doctorService.getDoctorById(id);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getId(), doctor.getId())
        );

    }

    @Test
    public void testThatGetDoctorByEmailReturnsDoctor() {
        // given
        String jwt = "jwt";
        String email = "email";
        DoctorEntity doctor = DoctorEntity.builder().firstName("First Name").build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(repository.findByUserEmail(email)).thenReturn(Optional.of(doctor));
        DoctorEntity result = doctorService.getDoctorByEmail(jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), doctor.getFirstName())
        );
    }

    @Test
    public void testThatPatchDoctorByEmailThrowsException() {
        // given
        String jwt = "jwt";
        String email = "email";
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(repository.findByUserEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> doctorService.patchDoctor(jwt, null));
        verify(repository, never()).save(any(DoctorEntity.class));
        verify(messageProducer, never()).sendDoctorToIndex(any(DoctorDocumentDto.class));
    }

    @Test
    public void testThatPatchDoctorByEmailWorks() {
        // given
        String jwt = "jwt";
        String email = "email";
        DoctorEntity doctor = DoctorEntity
                .builder()
                .firstName("First Name")
                .specializations(List.of())
                .build();
        DoctorEntity updated = DoctorEntity.builder()
                .firstName("UPDATED")
                .specializations(List.of())
                .build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(repository.findByUserEmail(email)).thenReturn(Optional.of(doctor));
        when(repository.save(updated)).thenReturn(updated);
        DoctorEntity result = doctorService.patchDoctor(jwt, updated);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), updated.getFirstName())
        );
        verify(repository, times(1)).save(updated);
        verify(messageProducer, times(1)).sendDoctorToIndex(any(DoctorDocumentDto.class));
    }

    @Test
    public void testThatDeleteDoctorByIdWorks() {
        // given
        Long id = 1L;
        // when
        doctorService.deleteDoctorById(id);
        // then
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    public void testThatGetDoctorsByParamReturnsDoctorDocument() {
        // given
        String params = "First Last";
        Pageable pageable = PageRequest.of(1, 1);
        DoctorDocument doctorDocument = DoctorDocument.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .experienceYears(5L)
                .clinicAddress("Clinic").build();
        Page<DoctorDocument> expected = new PageImpl<>(List.of(doctorDocument));
        // when
        when(elasticRepository.findByParams(params, pageable)).thenReturn(expected);
        Page<DoctorDocument> result = doctorService.getAllDoctorsByParam(params, pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected)
        );
        verify(elasticRepository, times(1)).findByParams(params, pageable);
    }

}
