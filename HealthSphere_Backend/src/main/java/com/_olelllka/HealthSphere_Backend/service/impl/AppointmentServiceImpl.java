package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.repositories.AppointmentRepository;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private AppointmentRepository repository;

    @Autowired
    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Page<AppointmentEntity> getAllAppointmentsForPatient(Long userId, Pageable pageable) {
        return repository.findByPatientId(userId, pageable);
    }

    @Override
    public Page<AppointmentEntity> getAllAppointmentsForDoctor(Long doctorId, Pageable pageable) {
        return repository.findByDoctorId(doctorId, pageable);
    }
}
