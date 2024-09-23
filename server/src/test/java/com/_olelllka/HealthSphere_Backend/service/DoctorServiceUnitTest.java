package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.DoctorHelperCachingService;
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
    @Mock
    private DoctorHelperCachingService cachingService;
    @InjectMocks
    private DoctorServiceImpl doctorService;

    @Test
    public void testThatGetAllDoctorsReturnPage() {
        // given
        Pageable pageable = PageRequest.of(1, 1);
        DoctorEntity entity = createDoctorEntity();
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
    public void testThatGetDoctorByIdReturnsDoctor() {
        // given
        Long id = 1L;
        DoctorEntity doctor = createDoctorEntity();
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
        DoctorEntity doctor = createDoctorEntity();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(cachingService.getDoctorByEmail(email)).thenReturn(doctor);
        DoctorEntity result = doctorService.getDoctorByEmail(jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), doctor.getFirstName())
        );
    }

    @Test
    public void testThatPatchDoctorWorks() {
        // given
        String jwt = "jwt";
        String email = "email";
        DoctorEntity updated = createDoctorEntity();
        updated.setFirstName("UPDATED");
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(cachingService.updateDoctorByEmail(email, updated)).thenReturn(updated);
        DoctorEntity result = doctorService.patchDoctor(jwt, updated);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), updated.getFirstName())
        );
        verify(messageProducer, times(1)).sendDoctorToIndex(any(DoctorDocumentDto.class));
    }

    @Test
    public void testThatDeleteDoctorWorks() {
        // given
        String jwt = "jwt";
        String email = "email";
        DoctorEntity doctor = createDoctorEntity();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(cachingService.getDoctorByEmail(email)).thenReturn(doctor);
        doctorService.deleteDoctor(jwt);
        // then
        verify(cachingService, times(1)).getDoctorByEmail(email);
        verify(cachingService, times(1)).deleteDoctorByEmail(email);
        verify(messageProducer, times(1)).deleteDoctorFromIndex(anyLong());
        verify(repository, times(1)).deleteById(anyLong());
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

    private DoctorEntity createDoctorEntity() {
        return DoctorEntity
                .builder()
                .id(1L)
                .firstName("First Name")
                .specializations(List.of())
                .build();
    }

}
