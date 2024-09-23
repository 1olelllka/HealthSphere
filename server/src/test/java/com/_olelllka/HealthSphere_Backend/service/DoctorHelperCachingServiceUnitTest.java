package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.DoctorHelperCachingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorHelperCachingServiceUnitTest {

    @Mock
    private DoctorRepository repository;
    @InjectMocks
    private DoctorHelperCachingService cachingService;

    @Test
    public void testThatGetDoctorByEmailThrowsNotFoundException() {
        // given
        String email = "email";
        // when
        when(repository.findByUserEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> cachingService.getDoctorByEmail(email));
    }

    @Test
    public void testThatGetDoctorByEmailReturnsDoctor() {
        // given
        String email = "email";
        DoctorEntity doctor = DoctorEntity.builder().id(1L).build();
        // when
        when(repository.findByUserEmail(email)).thenReturn(Optional.of(doctor));
        DoctorEntity result = cachingService.getDoctorByEmail(email);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getId(), doctor.getId())
        );
    }

    @Test
    public void testThatUpdateDoctorByEmailThrowsNotFoundException() {
        // given
        String email = "email";
        // when
        when(repository.findByUserEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> cachingService.updateDoctorByEmail(email, null));
        verify(repository, never()).save(any(DoctorEntity.class));
    }

    @Test
    public void testThatUpdateDoctorByEmailReturnsUpdatedDoctor() {
        // given
        String email = "email";
        DoctorEntity updated = DoctorEntity.builder().firstName("UPDATED").build();
        DoctorEntity doctor = DoctorEntity.builder().firstName("FIRST").build();
        // when
        when(repository.findByUserEmail(email)).thenReturn(Optional.of(doctor));
        when(repository.save(updated)).thenReturn(updated);
        DoctorEntity result = cachingService.updateDoctorByEmail(email, updated);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getFirstName(), updated.getFirstName())
        );
    }
}
