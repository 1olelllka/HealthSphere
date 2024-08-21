package com._olelllka.HealthSphere_Backend;

import com._olelllka.HealthSphere_Backend.domain.dto.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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

}
