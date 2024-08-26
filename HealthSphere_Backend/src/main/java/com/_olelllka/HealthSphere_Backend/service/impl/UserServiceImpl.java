package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.RegisterDoctorForm;
import com._olelllka.HealthSphere_Backend.domain.dto.RegisterPatientForm;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Role;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.repositories.UserRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private PatientRepository patientRepository;
    private DoctorRepository doctorRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           PatientRepository patientRepository,
                           DoctorRepository doctorRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserEntity getUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User with such username was not found."));
    }

    @Override
    public UserEntity register(RegisterPatientForm registerPatientForm) {
        UserEntity user = UserEntity.builder()
                .email(registerPatientForm.getEmail())
                .password(passwordEncoder.encode(registerPatientForm.getPassword()))
                .role(Role.ROLE_PATIENT).build();
        PatientEntity patient = PatientEntity.builder()
                .user(user)
                .firstName(registerPatientForm.getFirstName())
                .lastName(registerPatientForm.getLastName())
                .dateOfBirth(registerPatientForm.getDateOfBirth())
                .gender(registerPatientForm.getGender())
                .build();
        patientRepository.save(patient);
        return userRepository.save(user);
    }

    @Override
    public UserEntity doctorRegister(RegisterDoctorForm registerDoctorForm) {
        UserEntity user = UserEntity.builder()
                .email(registerDoctorForm.getEmail())
                .password(passwordEncoder.encode(registerDoctorForm.getPassword()))
                .role(Role.ROLE_DOCTOR).build();
        DoctorEntity patient = DoctorEntity.builder()
                .user(user)
                .firstName(registerDoctorForm.getFirstName())
                .lastName(registerDoctorForm.getLastName())
                .licenseNumber(registerDoctorForm.getLicenseNumber())
                .clinicAddress(registerDoctorForm.getClinicAddress())
                .phoneNumber(registerDoctorForm.getPhoneNumber())
                .build();
        doctorRepository.save(patient);
        return userRepository.save(user);
    }
}
