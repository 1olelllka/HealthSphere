package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {
    @Query("SELECT m FROM MedicalRecordEntity m JOIN m.patient p WHERE p.id = :id")
    Page<MedicalRecordEntity> findByPatientId(Long id, Pageable pageable);
}
