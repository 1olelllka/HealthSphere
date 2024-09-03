package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.repositories.AppointmentRepository;
import com._olelllka.HealthSphere_Backend.service.impl.AppointmentServiceImpl;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceUnitTest {

    @Mock
    private AppointmentRepository repository;
    @InjectMocks
    private AppointmentServiceImpl service;

    @Test
    public void testThatGetAllAppointmentsForPatientReturnsPageOfAppointments() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        Long userId = 1L;
        AppointmentEntity entity = AppointmentEntity.builder().id(1L).build();
        Page<AppointmentEntity> expected = new PageImpl<>(List.of(entity));
        // when
        when(repository.findByPatientId(userId, pageable)).thenReturn(expected);
        Page<AppointmentEntity> result = service.getAllAppointmentsForPatient(userId, pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), expected.getContent().size())
        );
    }

    @Test
    public void testThatGetAllAppointmentsForDoctorReturnsPageOfAppointments() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        Long doctorId = 1L;
        AppointmentEntity entity = AppointmentEntity.builder().id(1L).build();
        Page<AppointmentEntity> expected = new PageImpl<>(List.of(entity));
        // when
        when(repository.findByDoctorId(doctorId, pageable)).thenReturn(expected);
        Page<AppointmentEntity> result = service.getAllAppointmentsForDoctor(doctorId, pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), expected.getContent().size())
        );
    }
}
