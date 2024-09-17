package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import com._olelllka.HealthSphere_Backend.repositories.AppointmentRepository;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceUnitTest {

    @Mock
    private AppointmentRepository repository;
    @Mock
    private JwtService jwtService;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @InjectMocks
    private AppointmentServiceImpl service;

    @Test
    public void testThatGetAllAppointmentsForPatientReturnsPageOfAppointments() {
        // given
        Pageable pageable = PageRequest.of(0, 1);
        Long userId = 1L;
        AppointmentEntity entity = AppointmentEntity.builder().id(1L).build();
        List<AppointmentEntity> expected = List.of(entity);
        // when
        when(repository.findByPatientId(userId)).thenReturn(expected);
        List<AppointmentEntity> result = service.getAllAppointmentsForPatient(userId);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), expected.size())
        );
    }

    @Test
    public void testThatGetAllAppointmentsForDoctorReturnsPageOfAppointments() {
        // given
        Long doctorId = 1L;
        AppointmentEntity entity = AppointmentEntity.builder().id(1L).build();
        List<AppointmentEntity> expected = List.of(entity);
        // when
        when(repository.findByDoctorId(doctorId)).thenReturn(expected);
        List<AppointmentEntity> result = service.getAllAppointmentsForDoctor(doctorId);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), expected.size())
        );
    }

    @Test
    public void testThatGetAppointmentByIdThrowsException() {
        // given
        Long id = 1L;
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.getAppointmentById(id));
    }

    @Test
    public void testThatGetAppointmentReturnsAppointment() {
        // given
        Long id = 1L;
        AppointmentEntity expected = AppointmentEntity.builder().id(1L).build();
        // when
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        AppointmentEntity result = service.getAppointmentById(id);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected.getId(), result.getId())
        );
    }

    @Test
    public void testThatCreateAppointmentForPatientThrowsException() {
        // given
        String jwt = "jwt";
        String email = "email";
        AppointmentEntity entity = AppointmentEntity.builder().build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.createNewAppointmentForPatient(entity, jwt));
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    @Test
    public void testThatCreateAppointmentForDoctorThrowsException() {
        // given
        String jwt = "jwt";
        String email = "email";
        AppointmentEntity entity = AppointmentEntity.builder().build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.createNewAppointmentForDoctor(entity, jwt));
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    @Test
    public void testThatCreateAppointmentForPatientWorks() {
        // given
        String jwt = "jwt";
        String email = "email";
        AppointmentEntity entity = AppointmentEntity.builder().build();
        AppointmentEntity finalEntity = AppointmentEntity.builder()
                .status(Status.SCHEDULED)
                .patient(PatientEntity.builder().build()).build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(patientRepository.findByEmail(email)).thenReturn(Optional.of(PatientEntity.builder().build()));
        when(repository.save(finalEntity)).thenReturn(finalEntity);
        AppointmentEntity result = service.createNewAppointmentForPatient(entity, jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(finalEntity.getPatient(), result.getPatient())
        );
    }

    @Test
    public void testThatCreateAppointmentForDoctorWorks() {
        // given
        String jwt = "jwt";
        String email = "email";
        AppointmentEntity entity = AppointmentEntity.builder().build();
        AppointmentEntity finalEntity = AppointmentEntity.builder()
                .status(Status.SCHEDULED)
                .doctor(DoctorEntity.builder()
                        .build()).build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(doctorRepository.findByUserEmail(email)).thenReturn(Optional.of(DoctorEntity.builder().build()));
        when(repository.save(finalEntity)).thenReturn(finalEntity);
        AppointmentEntity result = service.createNewAppointmentForDoctor(entity, jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(finalEntity.getDoctor(), result.getDoctor())
        );
    }

    @Test
    public void testThatPatchAppointmentThrowsException() {
        // given
        Long id = 1L;
        AppointmentEntity entity = AppointmentEntity.builder().build();
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.updateEntity(entity, id));
        verify(repository, never()).save(any(AppointmentEntity.class));
    }

    @Test
    public void testThatPatchAppointmentWorks() {
        // given
        Long id = 1L;
        AppointmentEntity entity = AppointmentEntity.builder().build();
        AppointmentEntity updated = AppointmentEntity
                .builder()
                .reason("REASON")
                .build();
        // when
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(repository.save(updated)).thenReturn(updated);
        AppointmentEntity result = service.updateEntity(updated, id);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(updated.getReason(), result.getReason())
        );
    }

    @Test
    public void testThatDeleteAppointmentByIdWorks() {
        // given
        Long id = 1L;
        // when
        service.deleteById(id);
        // then
        verify(repository, times(1)).deleteById(id);
    }
}
