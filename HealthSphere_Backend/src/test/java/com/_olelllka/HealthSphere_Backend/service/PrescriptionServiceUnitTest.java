package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PrescriptionMedicineRepository;
import com._olelllka.HealthSphere_Backend.repositories.PrescriptionRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.PrescriptionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrescriptionServiceUnitTest {

    @Mock
    private PrescriptionRepository repository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PrescriptionMedicineRepository medicineRepository;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private PrescriptionServiceImpl service;

    @Test
    public void testThatCreatePrescriptionThrowsException() {
        String jwt = "jwt";
        PrescriptionEntity entity = PrescriptionEntity.builder().build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn("email");
        when(doctorRepository.findByUserEmail("email")).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.createPrescription(entity, jwt));
        verify(repository, never()).save(any(PrescriptionEntity.class));
    }

    @Test
    public void testThatCreatePrescriptionWorks() {
        // given
        String jwt = "jwt";
        DoctorEntity doctor = DoctorEntity.builder().build();
        PrescriptionEntity entity = PrescriptionEntity.builder().build();
        PrescriptionEntity expected = PrescriptionEntity.builder().doctor(doctor).build();
        // when
        when(repository.save(expected)).thenReturn(expected);
        when(jwtService.extractUsername(jwt)).thenReturn("email");
        when(doctorRepository.findByUserEmail("email")).thenReturn(Optional.of(doctor));
        PrescriptionEntity result = service.createPrescription(entity, jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, entity)
        );
    }

    @Test
    public void testThatAddMedicineToPrescriptionThrowsException() {
        // given
        Long id = 1L;
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder().build();
        // when
        when(repository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.addMedicineToPrescription(id, entity));
        verify(medicineRepository, never()).save(any(PrescriptionMedicineEntity.class));
    }

    @Test
    public void testThatAddMedicineToPrescriptionReturnsAddedMedicine() {
        // given
        Long id = 1L;
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder().build();
        PrescriptionEntity prescription = PrescriptionEntity.builder().build();
        PrescriptionMedicineEntity expected = PrescriptionMedicineEntity.builder().prescription(prescription).build();
        // when
        when(repository.findById(id)).thenReturn(Optional.of(prescription));
        when(medicineRepository.save(expected)).thenReturn(expected);
        PrescriptionMedicineEntity result = service.addMedicineToPrescription(id, entity);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(expected, result)
        );
    }

    @Test
    public void testThatGetAllMedicineByPrescriptionIdReturnsList() {
        // given
        Long id = 1L;
        List<PrescriptionMedicineEntity> expected = List.of(PrescriptionMedicineEntity.builder().build());
        // when
        when(medicineRepository.findByPrescriptionId(id)).thenReturn(expected);
        List<PrescriptionMedicineEntity> result = service.getAllMedicineToPrescription(id);
        // then
        assertEquals(result, expected);
    }

    @Test
    public void testThatUpdateMedicineByIdThrowsException() {
        // given
        Long id = 1L;
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder().build();
        // when
        when(medicineRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> service.updateTheMedicineById(id, entity));
        verify(medicineRepository, never()).save(any(PrescriptionMedicineEntity.class));
    }

    @Test
    public void testThatUpdateMedicineByIdReturnsUpdatedMedicine() {
        // given
        Long id = 1L;
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder()
                .dosage("DOSAGE")
                .instructions("INSTRUCTIONS")
                .medicineName("MEDICINE")
                .build();
        PrescriptionMedicineEntity updated = PrescriptionMedicineEntity.builder()
                .medicineName("UPDATED")
                .build();
        PrescriptionMedicineEntity expected = PrescriptionMedicineEntity.builder()
                .dosage("DOSAGE")
                .instructions("INSTRUCTIONS")
                .medicineName("UPDATED")
                .build();
        // when
        when(medicineRepository.findById(id)).thenReturn(Optional.of(entity));
        when(medicineRepository.save(expected)).thenReturn(expected);
        PrescriptionMedicineEntity result = service.updateTheMedicineById(id, updated);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected),
                () -> assertEquals(result.getMedicineName(), expected.getMedicineName())
        );
    }

    @Test
    public void testThatRemoveMedicineFromPrescriptionByIdWorks() {
        // given
        Long id = 1L;
        // when
        service.removeMedicineById(id);
        // then
        verify(medicineRepository, times(1)).deleteById(id);
    }

    @Test
    public void testThatDeleteWholePrescriptionWorks() {
        // given
        Long id = 1L;
        // when
        when(medicineRepository.findByPrescriptionId(1L)).thenReturn(List.of());
        service.deletePrescription(id);
        // then
        verify(medicineRepository, times(1)).deleteAll(List.of());
        verify(repository, times(1)).deleteById(id);
    }
}
