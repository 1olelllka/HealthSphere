package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordRepository;
import com._olelllka.HealthSphere_Backend.service.impl.MedicalRecordServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceUnitTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    @Test
    public void testThatMedicalRecordServiceReturnsListOfRecords() {
        // given
        String email = "someEmail@gmail.com";
        String jwt = "jwt";
        MedicalRecordEntity entity = MedicalRecordEntity.builder().diagnosis("Some Diagnosis").build();
        // when
        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(medicalRecordRepository.findByPatientEmail(email)).thenReturn(List.of(entity));
        List<MedicalRecordEntity> result = medicalRecordService.getAllRecordsForPatient(jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), 1),
                () -> assertEquals(result.get(0).getDiagnosis(), "Some Diagnosis")
        );
    }

}
