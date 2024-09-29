package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import com._olelllka.HealthSphere_Backend.repositories.AppointmentRepository;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.DuplicateException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.AppointmentService;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
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
        duplicateDoctor(doctor.getId(), entity.getAppointmentDate());
        entity.setDoctor(doctor);
        entity.setStatus(Status.SCHEDULED);
        return repository.save(entity);
    }

    @Override
    public AppointmentEntity createNewAppointmentForPatient(AppointmentEntity entity, String jwt) {
        String email = jwtService.extractUsername(jwt);
        PatientEntity patient = patientRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("The Patient with such email was not found."));
        duplicatePatient(patient.getId(), entity.getAppointmentDate());
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

    private void duplicatePatient(Long patientId, Date appointmentDate) {
        if (repository.findUniquePatientAndAppointmentDate(patientId, appointmentDate).isPresent()) {
            throw new DuplicateException("The appointment with such date already exists");
        }
    }

    private void duplicateDoctor(Long doctorId, Date appointmentDate) {
        if (repository.findUniqueDoctorAndAppointmentDate(doctorId, appointmentDate).isPresent()) {
            throw new DuplicateException("The appointment with such date already exists");
        }
    }

}
