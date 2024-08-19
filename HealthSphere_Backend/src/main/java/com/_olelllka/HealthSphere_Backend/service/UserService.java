package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.dto.RegisterForm;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;

public interface UserService {

    UserEntity getUserByUsername(String username);

    UserEntity register(RegisterForm registerForm);
}
