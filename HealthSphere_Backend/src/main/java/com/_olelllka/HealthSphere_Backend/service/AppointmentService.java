package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppointmentService {
    Page<AppointmentEntity> getAllAppointmentsForPatient(Long userId, Pageable pageable);

    Page<AppointmentEntity> getAllAppointmentsForDoctor(Long doctorId, Pageable pageable);

    AppointmentEntity getAppointmentById(Long id);

    void deleteById(Long id);

    AppointmentEntity createNewAppointmentForDoctor(AppointmentEntity entity, String substring);

    AppointmentEntity createNewAppointmentForPatient(AppointmentEntity entity, String substring);

    AppointmentEntity updateEntity(AppointmentEntity entity, Long id);
}
