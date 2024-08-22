package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class PatientRepositoryDataJpaTest {

    private PatientRepository patientRepository;

    @Autowired
    public PatientRepositoryDataJpaTest(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @BeforeEach
    public void setUp() throws ParseException {
        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .password("password")
                .role(Role.ROLE_PATIENT)
                .build();
        PatientEntity patient = PatientEntity.builder()
                .user(user)
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .address("Address Address")
                .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("24-01-2005"))
                .phoneNumber("123904810923849123")
                .build();
        patientRepository.save(patient);
    }

    @AfterEach
    public void tearDown() {
        patientRepository.deleteById(1L);
    }

    @Test
    public void testThatPatientWillNotBeFound() {
        Optional<PatientEntity> result = patientRepository.findByEmail("error@error.com");
        assertFalse(result.isPresent());
    }

    @Test
    public void testThatPatientWillBeFound() {
        Optional<PatientEntity> result = patientRepository.findByEmail("email@email.com");

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(result.get().getUser().getEmail(), "email@email.com")
        );
    }

}
