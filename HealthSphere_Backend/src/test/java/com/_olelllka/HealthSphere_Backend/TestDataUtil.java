package com._olelllka.HealthSphere_Backend;

import com._olelllka.HealthSphere_Backend.domain.dto.*;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class TestDataUtil {

    public static RegisterPatientForm createRegisterForm() throws ParseException {
        return RegisterPatientForm.builder()
                .email("email@email.com")
                .password("password123")
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .dateOfBirth(new SimpleDateFormat("yyyy-MM-dd").parse("2020-04-12")).build();
    }

    public static LoginForm createLoginForm() {
        return LoginForm.builder()
                .email("email@email.com")
                .password("password123")
                .build();
    }

    public static RegisterDoctorForm createRegisterDoctorForm() throws ParseException {
        return RegisterDoctorForm.builder()
                .email("email@email.com")
                .password("password123")
                .licenseNumber("12341090189123")
                .phoneNumber("123456789")
                .clinicAddress("West 12 St.")
                .firstName("First Name")
                .lastName("Last Name")
                .build();
    }

    public static PatientDto createPatientDto(UserDto userDto) throws ParseException{
        return PatientDto.builder()
                .user(userDto)
                .firstName("First Name")
                .lastName("Last Name")
                .gender(Gender.MALE)
                .address("Address Address")
                .dateOfBirth(new SimpleDateFormat("dd-MM-yyyy").parse("12-04-2004"))
                .phoneNumber("14123412341234")
                .build();
    }

    public static DoctorDetailDto createDoctorDetailDto(UserDto userDto) {
        return DoctorDetailDto.builder()
                .user(userDto)
                .firstName("Doctor First")
                .lastName("Doctor Last")
                .clinicAddress("Address")
                .experienceYears(2L)
                .specializations(List.of())
                .licenseNumber("12390123123")
                .phoneNumber("13422341234")
                .build();
    }

    public static UserDto createUserDto() {
        return UserDto.builder()
                .email("email@email.com")
                .role(Role.ROLE_PATIENT)
                .build();
    }
}
