package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.dto.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;

public interface UserService {

    UserEntity getUserByUsername(String username);

    UserEntity register(RegisterPatientForm registerPatientForm);

    UserEntity doctorRegister(RegisterDoctorForm registerDoctorForm);
}
