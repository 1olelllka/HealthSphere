package com._olelllka.HealthSphere_Backend.repositories;

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
public class UserRepositoryDataJpaTest {

    private UserRepository userRepository;

    @Autowired
    public UserRepositoryDataJpaTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        UserEntity user = UserEntity.builder()
                .email("email@email.com")
                .password("password")
                .role(Role.ROLE_PATIENT)
                .build();
        userRepository.save(user);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteById(1L);
    }

    @Test
    public void testThatNonExistentUserWillNotBeFound() {
        Optional<UserEntity> user = userRepository.findByEmail("falseEmail@email.com");
        assertFalse(user.isPresent());
    }

    @Test
    public void testThatExistentUserWillBeFound() {
        Optional<UserEntity> user = userRepository.findByEmail("email@email.com");

        assertAll(
                () -> assertTrue(user.isPresent()),
                () -> assertEquals(user.get().getEmail(), "email@email.com")
        );
    }
}
