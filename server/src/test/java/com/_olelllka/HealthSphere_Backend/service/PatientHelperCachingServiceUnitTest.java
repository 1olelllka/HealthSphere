package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.PatientHelperCachingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientHelperCachingServiceUnitTest {

    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private PatientHelperCachingService cachingService;

    @Test
    public void testThatGetPatientByUsernameThrowsNotFoundException() {
        // given
        String email = "email";
        // when
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> cachingService.getPatientByUsername(email));
    }

    @Test
    public void testThatGetPatientByUsernameReturnsPatient() {
        // given
        String email = "email";
        PatientEntity expected = PatientEntity.builder().id(1L).build();
        // when
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(expected));
        PatientEntity result = cachingService.getPatientByUsername(email);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected.getId(), result.getId())
        );
    }

    @Test
    public void testThatPatchPatientByEmailThrowsNotFoundException() {
        // given
        String email = "email";
        // when
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> cachingService.patchPatientByEmail(email, null));
        verify(patientRepository, never()).save(any(PatientEntity.class));
    }

    @Test
    public void testThatPatchPatientUpdatesThePatient() {
        // given
        String email = "email";
        PatientEntity original = PatientEntity.builder().firstName("FirstName").build();
        PatientEntity updated = PatientEntity.builder().firstName("UPDATED").build();
        // when
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(original));
        when(patientRepository.save(updated)).thenReturn(updated);
        PatientEntity result = cachingService.patchPatientByEmail(email, updated);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), updated.getFirstName())
        );
    }

}
