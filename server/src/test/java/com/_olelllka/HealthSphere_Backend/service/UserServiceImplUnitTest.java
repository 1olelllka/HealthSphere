package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.TestDataUtil;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.repositories.UserRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.UserServiceImpl;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.DoctorMessageProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.util.List;
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
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorMessageProducer messageProducer;
    @Mock
    private SpecializationMapper mapper;
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
    public void testThatPatientRegisterWorksWell() throws ParseException {
        // given
        RegisterPatientForm registerPatientForm = TestDataUtil.createRegisterForm();
        UserEntity user = UserEntity.builder()
                .email(registerPatientForm.getEmail())
                        .role(Role.ROLE_PATIENT)
                                .build();
        // when
        when(userRepository.save(any())).thenReturn(user);
        when(patientRepository.save(any())).thenReturn(null);
        when(passwordEncoder.encode("password123")).thenReturn("encrypted");
        UserEntity result = userService.register(registerPatientForm);
        // then
        verify(userRepository, times(1)).save(any());
        verify(patientRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(anyString());
        assertEquals(result.getEmail(), user.getEmail());
        assertNotEquals(result.getPassword(), registerPatientForm.getPassword());
    }

    @Test
    public void testThatDoctorRegisterWorksWell() throws ParseException {
        // given
        SpecializationDto specializationDto = SpecializationDto.builder().id(1L).specializationName("NAME").build();
        SpecializationEntity specializationEntity = SpecializationEntity.builder().id(1L).specializationName("NAME").build();
        RegisterDoctorForm registerDoctorForm = TestDataUtil.createRegisterDoctorForm();
        registerDoctorForm.setSpecializations(List.of(specializationDto));
        UserEntity user = UserEntity.builder()
                .email(registerDoctorForm.getEmail())
                .role(Role.ROLE_DOCTOR)
                .build();
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("First Name")
                .lastName("Last Name")
                .licenseNumber("123415")
                .clinicAddress("4313241")
                .specializations(List.of(specializationEntity))
                .phoneNumber("412341234").build();
        // when
        when(userRepository.save(any())).thenReturn(user);
        when(doctorRepository.save(any())).thenReturn(doctor);
        when(mapper.toEntity(specializationDto)).thenReturn(specializationEntity);
        when(passwordEncoder.encode("password123")).thenReturn("encrypted");
        UserEntity result = userService.doctorRegister(registerDoctorForm);
        // then
        verify(userRepository, times(1)).save(any());
        verify(doctorRepository, times(1)).save(any());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(messageProducer, times(1)).sendDoctorToIndex(any(DoctorDocumentDto.class));
        assertEquals(result.getEmail(), user.getEmail());
        assertNotEquals(result.getPassword(), registerDoctorForm.getPassword());
    }
}
