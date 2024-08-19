package com._olelllka.HealthSphere_Backend;

import com._olelllka.HealthSphere_Backend.domain.dto.LoginForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterForm;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestDataUtil {

    public static RegisterForm createRegisterForm() throws ParseException {
        return RegisterForm.builder()
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

}
