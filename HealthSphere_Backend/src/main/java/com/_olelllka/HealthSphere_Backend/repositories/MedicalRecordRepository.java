package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {

    @Query("SELECT m FROM MedicalRecordEntity m JOIN m.patient.user u WHERE u.email = :email")
    List<MedicalRecordEntity> findByPatientEmail(String email);
}
