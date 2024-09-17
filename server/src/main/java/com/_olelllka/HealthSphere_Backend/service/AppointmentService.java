package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AppointmentService {
    List<AppointmentEntity> getAllAppointmentsForPatient(Long userId);

    List<AppointmentEntity> getAllAppointmentsForDoctor(Long doctorId);

    AppointmentEntity getAppointmentById(Long id);

    void deleteById(Long id);

    AppointmentEntity createNewAppointmentForDoctor(AppointmentEntity entity, String substring);

    AppointmentEntity createNewAppointmentForPatient(AppointmentEntity entity, String substring);

    AppointmentEntity updateEntity(AppointmentEntity entity, Long id);
}
