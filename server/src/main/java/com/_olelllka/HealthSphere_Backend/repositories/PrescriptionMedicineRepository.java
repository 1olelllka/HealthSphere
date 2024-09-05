package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrescriptionMedicineRepository extends JpaRepository<PrescriptionMedicineEntity, Long> {
    @Query("SELECT p FROM PrescriptionMedicineEntity p JOIN p.prescription r WHERE r.id = :id")
    List<PrescriptionMedicineEntity> findByPrescriptionId(Long id);
}
