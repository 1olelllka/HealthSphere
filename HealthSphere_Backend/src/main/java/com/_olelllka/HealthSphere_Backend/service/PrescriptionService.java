package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;

import java.util.List;

public interface PrescriptionService {


    PrescriptionEntity createPrescription(PrescriptionEntity entity, String substring);

    PrescriptionMedicineEntity addMedicineToPrescription(Long id, PrescriptionMedicineEntity entity);

    List<PrescriptionMedicineEntity> getAllMedicineToPrescription(Long id);

    void removeMedicineById(Long id);

    PrescriptionMedicineEntity updateTheMedicineById(Long id, PrescriptionMedicineEntity entity);
}
