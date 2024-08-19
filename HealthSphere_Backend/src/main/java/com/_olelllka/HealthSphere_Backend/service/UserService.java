package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;

import java.util.Optional;

public interface UserService {

    UserEntity getUserByUsername(String username);

}
