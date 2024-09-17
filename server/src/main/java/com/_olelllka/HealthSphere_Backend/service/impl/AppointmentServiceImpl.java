package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import com._olelllka.HealthSphere_Backend.repositories.AppointmentRepository;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository repository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;
    private JwtService jwtService;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository repository,
                                  PatientRepository patientRepository,
                                  DoctorRepository doctorRepository,
                                  JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<AppointmentEntity> getAllAppointmentsForPatient(Long userId) {
        return repository.findByPatientId(userId);
    }

    @Override
    public List<AppointmentEntity> getAllAppointmentsForDoctor(Long doctorId) {
        return repository.findByDoctorId(doctorId);
    }

    @Override
    public AppointmentEntity getAppointmentById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Appointment with such id was not found."));
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public AppointmentEntity createNewAppointmentForDoctor(AppointmentEntity entity, String jwt) {
        String email = jwtService.extractUsername(jwt);
        DoctorEntity doctor = doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new NotFoundException("The doctor with such email was not found."));
        entity.setDoctor(doctor);
        entity.setStatus(Status.SCHEDULED);
        return repository.save(entity);
    }

    @Override
    public AppointmentEntity createNewAppointmentForPatient(AppointmentEntity entity, String jwt) {
        String email = jwtService.extractUsername(jwt);
        PatientEntity patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The Patient with such email was not found."));
        entity.setPatient(patient);
        entity.setStatus(Status.SCHEDULED);
        return repository.save(entity);
    }

    @Override
    public AppointmentEntity updateEntity(AppointmentEntity entity, Long id) {
        return repository.findById(id).map(appointment -> {
            Optional.ofNullable(entity.getReason()).ifPresent(appointment::setReason);
            Optional.ofNullable(entity.getStatus()).ifPresent(appointment::setStatus);
            return repository.save(appointment);
        }).orElseThrow(() -> new NotFoundException("Appointment with such id was not found."));
    }

}
