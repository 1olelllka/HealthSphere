package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {

    @Query("SELECT a FROM AppointmentEntity a JOIN a.patient p WHERE p.id = :userId")
    Page<AppointmentEntity> findByPatientId(Long userId, Pageable pageable);

    @Query("SELECT a FROM AppointmentEntity a JOIN a.doctor d WHERE d.id = :doctorId")
    Page<AppointmentEntity> findByDoctorId(Long doctorId, Pageable pageable);

    @Query("SELECT a FROM AppointmentEntity a JOIN a.patient p WHERE p.id = :patientId AND a.appointmentDate = :appointmentDate")
    Optional<AppointmentEntity> findUniquePatientAndAppointmentDate(Long patientId, Date appointmentDate);

    @Query("SELECT a FROM AppointmentEntity a JOIN a.doctor d WHERE d.id = :doctorId AND a.appointmentDate = :appointmentDate")
    Optional<AppointmentEntity> findUniqueDoctorAndAppointmentDate(Long doctorId, Date appointmentDate);

    @Query("SELECT a FROM AppointmentEntity a JOIN a.doctor d WHERE d.id = :doctorId AND a.appointmentDate BETWEEN :from AND :to")
    List<AppointmentEntity> findByDoctorIdAndParams(Long doctorId, Timestamp from, Timestamp to);

    @Query("SELECT a FROM AppointmentEntity a JOIN a.patient p WHERE p.id = :patientId AND a.appointmentDate BETWEEN :from AND :to")
    List<AppointmentEntity> findByPatientIdAndParams(Long patientId, Timestamp from, Timestamp to);
}
