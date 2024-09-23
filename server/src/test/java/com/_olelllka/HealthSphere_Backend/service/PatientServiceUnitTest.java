package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.PatientHelperCachingService;
import com._olelllka.HealthSphere_Backend.service.impl.PatientServiceImpl;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.PatientMessageProducer;
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
public class PatientServiceUnitTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PatientElasticRepository elasticRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private PatientHelperCachingService cachingService;
    @Mock
    private PatientMessageProducer messageProducer;
    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    public void testThatGetPatientReturnsCorrectUser() {
        // given
        String jwt = "jwt-token";
        PatientEntity patient = PatientEntity.builder().firstName("First Name").build();
        when(jwtService.extractUsername(jwt)).thenReturn("username");
        when(cachingService.getPatientByUsername("username")).thenReturn(patient);
        PatientEntity result = patientService.getPatient(jwt);
        // then
        assertAll(
                () -> assertEquals(patient.getAddress(), result.getAddress())
        );
    }

    @Test
    public void testThatGetAllPatientsWithoutParamsReturnsPageOfResults() {
        // given
        PatientDocument patientDocument = PatientDocument.builder().build();
        Pageable pageable = PageRequest.of(0, 1);
        // when
        when(elasticRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(patientDocument)));
        Page<PatientDocument> result = patientService.getAllPatients("", pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTotalElements(), 1)
        );
    }

    @Test
    public void testThatGetAllPatientsWithParamsReturnsPageOfResults() {
        // given
        String params = "params";
        PatientDocument patientDocument = PatientDocument.builder().build();
        Pageable pageable = PageRequest.of(0, 1);
        // when
        when(elasticRepository.findByParams(params, pageable)).thenReturn(new PageImpl<>(List.of(patientDocument)));
        Page<PatientDocument> result = patientService.getAllPatients(params, pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getTotalElements(), 1)
        );
    }

    @Test
    public void testThatPatchPatientDoesNotExecuteFurtherCodeIfCachingFails() {
        // given
        String jwt = "jwt";
        String email = "email";
        PatientEntity updatedEntity = PatientEntity.builder()
                .user(UserEntity.builder().email("email").build())
                .firstName("Updated Name").build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(cachingService.patchPatientByEmail(email, updatedEntity)).thenThrow(NotFoundException.class);
        // then
        assertThrows(NotFoundException.class, () -> patientService.patchPatient(jwt, updatedEntity));
        verify(messageProducer, never()).sendMessageCreatePatient(any(PatientListDto.class));
    }

    @Test
    public void testThatPatchPatientWorksWell() {
        // given
        String jwt = "jwt";
        String email = "email";
        PatientEntity updatedEntity = PatientEntity.builder()
                .user(UserEntity.builder().email("email").build())
                .firstName("Updated Name").build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(cachingService.patchPatientByEmail(email, updatedEntity)).thenReturn(updatedEntity);
        PatientEntity result = patientService.patchPatient(jwt, updatedEntity);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), updatedEntity.getFirstName())
        );
        verify(messageProducer, times(1)).sendMessageCreatePatient(any(PatientListDto.class));
    }

    @Test
    public void testThatGetPatientByIdThrowsException() {
        // given
        Long id = 1L;
        // when
        when(patientRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> patientService.getPatientById(id));
    }

    @Test
    public void testThatDeletePatientWorksWell() {
        // given
        String jwt = "jwt";
        String email = "email";
        PatientEntity patient = PatientEntity.builder()
                .id(1L).build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(cachingService.getPatientByUsername(email)).thenReturn(patient);
        patientService.deletePatient(jwt);
        // then
        verify(patientRepository, times(1)).deleteById(anyLong());
        verify(messageProducer, times(1)).sendMessageDeletePatient(1L);
    }
}
