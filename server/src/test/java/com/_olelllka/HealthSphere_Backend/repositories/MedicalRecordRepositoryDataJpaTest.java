package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Month;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MedicalRecordRepositoryDataJpaTest {

    private MedicalRecordRepository medicalRecordRepository;
    private PatientRepository patientRepository;
    private UserRepository userRepository;
    private DoctorRepository doctorRepository;

    @Autowired
    public MedicalRecordRepositoryDataJpaTest(MedicalRecordRepository medicalRecordRepository,
                                              PatientRepository patientRepository,
                                              DoctorRepository doctorRepository,
                                              UserRepository userRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }


    @AfterEach
    public void tearDown() {
        medicalRecordRepository.deleteAll();
        patientRepository.deleteAll();
        userRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    public void testThatMedicalRecordRepositoryFindsRecordsByPatientId() {
        UserEntity user = UserEntity.builder().email("someEmail1@email.com").role(Role.ROLE_PATIENT).build();
        PatientEntity patient = PatientEntity.builder().user(user)
                .firstName("Patient")
                .lastName("Last Name")
                .dateOfBirth(new Date())
                .gender(Gender.MALE)
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("Doctor")
                .lastName("Last Name Doctor")
                .licenseNumber("1234123412341234")
                .gender(Gender.MALE)
                .build();
        MedicalRecordEntity medicalRecord = MedicalRecordEntity
                .builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis("Some diagnosis")
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .treatment("Some treatment")
                .build();
        patientRepository.save(patient);
        userRepository.save(user);
        doctorRepository.save(doctor);
        medicalRecordRepository.save(medicalRecord);
        Pageable pageable = PageRequest.of(0, 1);
        Page<MedicalRecordEntity> result = medicalRecordRepository.findByPatientId(patient.getId(), pageable);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getPatient().getFirstName(), "Patient")
        );
    }

    @Test
    public void testThatMedicalRecordRepositoryReturnsEmptyListOfRecords() {
        Long id = 18L;
        Pageable pageable = PageRequest.of(0, 1);
        Page<MedicalRecordEntity> result = medicalRecordRepository.findByPatientId(id, pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 0)
        );
    }

}
