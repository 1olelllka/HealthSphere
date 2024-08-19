package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterForm;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.repositories.UserRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplUnitTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void testThatGetUserByUsernameThrowsNotFoundException() {
        // given
        String incorrectEmail = "incorrectEmail@email.com";
        // when
        when(userRepository.findByEmail(incorrectEmail)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> userService.getUserByUsername(incorrectEmail));
    }

    @Test
    public void testThatGetUserByUsernameReturnsUser() {
        // given
        String correctEmail = "email@email.com";
        UserEntity user = UserEntity.builder().email(correctEmail).password("password").role(Role.ROLE_PATIENT).build();
        // when
        when(userRepository.findByEmail(correctEmail)).thenReturn(Optional.of(user));
        UserEntity result = userService.getUserByUsername(correctEmail);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getEmail(), correctEmail)
        );
        verify(userRepository, times(1)).findByEmail(correctEmail);
    }

    @Test
    public void testThatUserRegisterWorksWell() throws ParseException {
        // given
        RegisterForm registerForm = TestDataUtil.createRegisterForm();
        UserEntity user = UserEntity.builder()
                .email(registerForm.getEmail())
                        .role(Role.ROLE_PATIENT)
                                .build();
        // when
        when(userRepository.save(any())).thenReturn(user);
        when(patientRepository.save(any())).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encrypted");
        UserEntity result = userService.register(registerForm);
        // then
        verify(userRepository, times(1)).save(any());
        verify(patientRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(anyString());
        assertEquals(result.getEmail(), user.getEmail());
        assertNotEquals(result.getPassword(), registerForm.getPassword());
    }
}
