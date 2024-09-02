package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class PrescriptionRepositoryDataJpaTest {

    @Autowired
    private PrescriptionRepository repository;
    @Autowired
    private PrescriptionMedicineRepository medicineRepository;

    @BeforeEach
    void setUp() {
        PrescriptionEntity prescription = PrescriptionEntity.builder().id(1L).build();
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder()
                .prescription(prescription)
                .dosage("DOSAGE")
                .medicineName("NAME")
                .build();
        repository.save(prescription);
        medicineRepository.save(entity);
    }

    @Test
    public void testThatFindByPrescriptionIdReturnsListOfMedicine() {
        PrescriptionEntity prescription = PrescriptionEntity.builder().id(1L).build();
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder()
                .id(1L)
                .prescription(prescription)
                .medicineName("NAME")
                .dosage("DOSAGE")
                .build();
        List<PrescriptionMedicineEntity> expected = List.of(entity);
        List<PrescriptionMedicineEntity> result = medicineRepository.findByPrescriptionId(1L);
        assertEquals(result, expected);
    }

}
