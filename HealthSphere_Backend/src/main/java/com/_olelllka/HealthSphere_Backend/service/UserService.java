package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.auth.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;

public interface UserService {

    UserEntity getUserByUsername(String username);

    UserEntity register(RegisterPatientForm registerPatientForm);

    UserEntity doctorRegister(RegisterDoctorForm registerDoctorForm);
}
