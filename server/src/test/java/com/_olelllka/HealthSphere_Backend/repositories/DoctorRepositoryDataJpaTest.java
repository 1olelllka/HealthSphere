package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DoctorRepositoryDataJpaTest {

    private DoctorRepository doctorRepository;
    private UserRepository userRepository;

    @Autowired
    public DoctorRepositoryDataJpaTest(DoctorRepository doctorRepository,
                                       UserRepository userRepository) {
        this.doctorRepository = doctorRepository;
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        UserEntity user = UserEntity.builder()
                .email("email")
                .password("password")
                .role(Role.ROLE_DOCTOR).build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .licenseNumber("123434132")
                .gender(Gender.MALE)
                .user(user).build();
        doctorRepository.save(doctor);
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        doctorRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testThatFindDoctorByEmailWorksWellIfDoctorExists() {
        Optional<DoctorEntity> result = doctorRepository.findByUserEmail("email");

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(result.get().getFirstName(), "First Name")
        );
    }

    @Test
    public void testThatFindDoctorByEmailReturnsOptionalOfEmpty() {
        Optional<DoctorEntity> result = doctorRepository.findByUserEmail("incorrect");

        assertTrue(result.isEmpty());
    }
}
