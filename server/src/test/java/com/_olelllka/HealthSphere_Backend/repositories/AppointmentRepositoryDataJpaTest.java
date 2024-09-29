package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppointmentRepositoryDataJpaTest {

    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;

    @AfterEach
    void tearDown() {
        patientRepository.deleteAll();
        doctorRepository.deleteAll();
        appointmentRepository.deleteAll();
    }

    @Test
    public void testThatFindByPatientIdReturnsPageOfAppointments() {
        PatientEntity patient = PatientEntity.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new Date())
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("FIRST NAME")
                .lastName("LAST NAME")
                .licenseNumber("12312")
                .gender(Gender.MALE)
                .build();
        DoctorEntity savedDoctor = doctorRepository.save(doctor);
        PatientEntity savedPatient = patientRepository.save(patient);
        AppointmentEntity appointment = AppointmentEntity
                .builder()
                .appointmentDate(new Date())
                .patient(savedPatient)
                .doctor(savedDoctor)
                .build();
        appointmentRepository.save(appointment);
        List<AppointmentEntity> result = appointmentRepository.findByPatientId(savedPatient.getId());
        List<AppointmentEntity> expected = List.of(AppointmentEntity.builder().build());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), expected.size())
        );
    }

    @Test
    public void testThatFindByDoctorIdReturnsPageOfAppointments() {
        PatientEntity patient = PatientEntity.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new Date())
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("FIRST NAME")
                .lastName("LAST NAME")
                .gender(Gender.MALE)
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
        Pageable pageable = PageRequest.of(0, 1);
        List<AppointmentEntity> result = appointmentRepository.findByDoctorId(savedDoctor.getId());
        List<AppointmentEntity> expected = List.of(AppointmentEntity.builder().build());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), expected.size())
        );
    }

    @Test
    public void testThatFindUniquePatientAndAppointmentDateReturnsOptionalOfResult() {
        PatientEntity patient = PatientEntity.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new Date())
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("FIRST NAME")
                .lastName("LAST NAME")
                .gender(Gender.MALE)
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
        AppointmentEntity expected = appointmentRepository.save(appointment);
        Optional<AppointmentEntity> result = appointmentRepository.findUniquePatientAndAppointmentDate(savedPatient.getId(), appointment.getAppointmentDate());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.get().getId(), expected.getId())
        );
    }

    @Test
    public void testThatFindUniquePatientAndAppointmentDateReturnsOptionalOfNull() throws ParseException {
        Optional<AppointmentEntity> result = appointmentRepository.findUniquePatientAndAppointmentDate(123L, new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-02"));

        assertTrue(result.isEmpty());
    }

    @Test
    public void testThatFindUniqueDoctorAndAppointmentDateReturnsOptionalOfResult() {
        PatientEntity patient = PatientEntity.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new Date())
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("FIRST NAME")
                .lastName("LAST NAME")
                .gender(Gender.MALE)
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
        AppointmentEntity expected = appointmentRepository.save(appointment);
        Optional<AppointmentEntity> result = appointmentRepository.findUniqueDoctorAndAppointmentDate(savedDoctor.getId(), appointment.getAppointmentDate());

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.get().getId(), expected.getId())
        );
    }

    @Test
    public void testThatFindUniqueDoctorAndAppointmentDateReturnsOptionalOfNull() throws ParseException {
        Optional<AppointmentEntity> result = appointmentRepository.findUniqueDoctorAndAppointmentDate(123L, new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-02"));

        assertTrue(result.isEmpty());
    }

}
