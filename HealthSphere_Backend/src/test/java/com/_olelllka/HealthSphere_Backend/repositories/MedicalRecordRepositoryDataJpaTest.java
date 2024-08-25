package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

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

    @BeforeEach
    public void setUp() {
        UserEntity user = UserEntity.builder().email("someEmail@email.com").role(Role.ROLE_PATIENT).build();
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
                .build();
        MedicalRecordEntity medicalRecord = MedicalRecordEntity
                .builder()
                .patient(patient)
                .doctor(doctor)
                .diagnosis("Some diagnosis")
                .recordDate(new Date())
                .treatment("Some treatment")
                .build();
        patientRepository.save(patient);
        userRepository.save(user);
        doctorRepository.save(doctor);
        medicalRecordRepository.save(medicalRecord);
    }

    @AfterEach
    public void tearDown() {
        medicalRecordRepository.deleteAll();
        patientRepository.deleteAll();
        userRepository.deleteAll();
        doctorRepository.deleteAll();
    }

    @Test
    public void testThatMedicalRecordRepositoryFindsRecordsByPatientEmail() {
        String email = "someEmail@email.com";
        List<MedicalRecordEntity> result = medicalRecordRepository.findByPatientEmail(email);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), 1),
                () -> assertEquals(result.get(0).getPatient().getFirstName(), "Patient")
        );
    }

    @Test
    public void testThatMedicalRecordRepositoryReturnsEmptyListOfRecords() {
        String email = "notCorrectEmail@email.com";
        List<MedicalRecordEntity> result = medicalRecordRepository.findByPatientEmail(email);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), 0)
        );
    }

}
