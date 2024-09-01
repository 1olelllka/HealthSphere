package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PatientServiceUnitTest {

    @Mock
    private PatientRepository patientRepository;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private PatientServiceImpl patientService;

    @Test
    public void testThatGetPatientThrowsNotFoundErrorIFNotFound() {
        // given
        String jwt = "jwt-token";
        // when
        when(jwtService.extractUsername(jwt)).thenReturn("username");
        when(patientRepository.findByEmail("username")).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> patientService.getPatient(jwt));
        verify(jwtService, times(1)).extractUsername(jwt);
        verify(patientRepository, times(1)).findByEmail("username");
    }

    @Test
    public void testThatGetPatientReturnsCorrectUser() {
        // given
        String jwt = "jwt-token";
        PatientEntity patient = PatientEntity.builder().firstName("First Name").build();
        when(jwtService.extractUsername(jwt)).thenReturn("username");
        when(patientRepository.findByEmail("username")).thenReturn(Optional.of(patient));
        PatientEntity result = patientService.getPatient(jwt);
        // then
        assertAll(
                () -> assertEquals(patient.getAddress(), result.getAddress())
        );
    }

    @Test
    public void testThatPatchPatientReturnsNotFoundException() {
        // given
        Long id = 1L;
        // when
        when(patientRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> patientService.patchPatient(id, null));
        verify(patientRepository, times(1)).findById(id);
        verify(patientRepository, never()).save(any());
    }

    @Test
    public void testThatPatchPatientWorksWell() {
        // given
        Long id = 1L;
        PatientEntity originalEntity = PatientEntity.builder().firstName("First Name").build();
        PatientEntity updatedEntity = PatientEntity.builder().firstName("Updated Name").build();
        // when
        when(patientRepository.findById(id)).thenReturn(Optional.of(originalEntity));
        when(patientRepository.save(updatedEntity)).thenReturn(updatedEntity);
        PatientEntity result = patientService.patchPatient(id, updatedEntity);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), updatedEntity.getFirstName())
        );
        verify(patientRepository, times(1)).findById(id);
        verify(patientRepository, times(1)).save(updatedEntity);
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
        Long id = 1L;
        // when
        patientService.deleteByEmail(id);
        // then
        verify(patientRepository, times(1)).deleteById(1L);
    }
}
