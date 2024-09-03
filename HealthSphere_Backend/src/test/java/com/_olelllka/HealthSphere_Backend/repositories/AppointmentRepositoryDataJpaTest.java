package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
public class AppointmentRepositoryDataJpaTest {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;


    @BeforeEach
    void setUp() {
        PatientEntity patient = PatientEntity.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new Date())
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .id(1L)
                .firstName("FIRST NAME")
                .lastName("LAST NAME")
                .licenseNumber("12312")
                .build();
        DoctorEntity savedDoctor = doctorRepository.save(doctor);
        PatientEntity savedPatient = patientRepository.save(patient);
        AppointmentEntity appointment = AppointmentEntity
                .builder()
                .id(1L)
                .appointmentDate(new Date())
                .patient(savedPatient)
                .doctor(savedDoctor)
                .build();
        appointmentRepository.save(appointment);
    }

    @AfterEach
    void tearDown() {
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        appointmentRepository.deleteAll();
    }

    @Test
    public void testThatFindByPatientIdReturnsPageOfAppointments() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<AppointmentEntity> result = appointmentRepository.findByPatientId(1L, pageable);
        Page<AppointmentEntity> expected = new PageImpl<>(List.of(AppointmentEntity.builder().build()));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), expected.getContent().size())
        );
    }

    @Test
    public void testThatFindByDoctorIdReturnsPageOfAppointments() {
        Pageable pageable = PageRequest.of(0, 1);
        Page<AppointmentEntity> result = appointmentRepository.findByDoctorId(2L, pageable);
        Page<AppointmentEntity> expected = new PageImpl<>(List.of(AppointmentEntity.builder().build()));

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), expected.getContent().size())
        );
    }

}
