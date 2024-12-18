package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.AccessDeniedException;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.impl.MedicalRecordServiceImpl;
import com._olelllka.HealthSphere_Backend.service.rabbitmq.MedicalRecordMessageProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordServiceUnitTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private MedicalRecordElasticRepository elasticRepository;
    @Mock
    private MedicalRecordMessageProducer messageProducer;
    @Mock
    private JwtService jwtService;
    @Mock
    private DoctorRepository doctorRepository;
    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;

    @Test
    public void testThatMedicalRecordServiceReturnsListOfRecords() {
        // given
        Long id = 1L;
        Pageable pageable = PageRequest.of(0, 1);
        MedicalRecordEntity entity = createMedicalRecordEntity(null, null);
        // when
        when(medicalRecordRepository.findByPatientId(id, pageable)).thenReturn(new PageImpl<>(List.of(entity)));
        Page<MedicalRecordEntity> result = medicalRecordService.getAllRecordsByPatientId(id, pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getDiagnosis(), "Diagnosis")
        );
    }

    @Test
    public void testThatMedicalRecordServiceReturnsWithParamsListOfRecords() {
        // given
        Long id = 1L;
        String diagnosis = "some diagnosis";
        String from = "2020-02-02";
        String to = "2021-02-02";
        Pageable pageable = PageRequest.of(0, 1);
        MedicalRecordDocument document = MedicalRecordDocument.builder().diagnosis("Some Diagnosis").build();
        // when
        when(elasticRepository.findByParams(id, diagnosis, from.toString(), to.toString(), pageable)).thenReturn(new PageImpl<>(List.of(document)));
        Page<MedicalRecordDocument> result = medicalRecordService.getRecordsByParams(id, diagnosis, from, to, pageable);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getDiagnosis(), "Some Diagnosis")
        );
    }


    @Test
    public void testThatGetMedicalRecordForPatientByIdThrowsNotFoundError() {
        // given
        Long id = 1L;
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> medicalRecordService.getDetailedMedicalRecordForPatient(id));
    }

    @Test
    public void testThatGetMedicalRecordForPatientByIdReturnsMedicalRecord() {
        // given
        Long id = 1L;
        MedicalRecordEntity medicalRecordEntity = createMedicalRecordEntity(null, null);
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.of(medicalRecordEntity));
        MedicalRecordEntity result = medicalRecordService.getDetailedMedicalRecordForPatient(id);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getDiagnosis(), medicalRecordEntity.getDiagnosis())
        );
    }

    @Test
    public void testThatPatchMedicalForPatientThrowsNotFoundException() {
        // given
        Long id = 1L;
        MedicalRecordEntity entity = createMedicalRecordEntity(null, null);
        String jwt = "jwt";
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> medicalRecordService.patchMedicalRecordForPatient(id, entity, jwt));
        verify(jwtService, never()).extractUsername(jwt);
    }

    @Test
    public void testThatPatchMedicalRecordForPatientThrowsNotAuthorizedException() {
        // given
        Long id = 1L;
        String jwt = "jwt";
        MedicalRecordEntity entity = createMedicalRecordEntity(null, DoctorEntity
                .builder()
                .user(UserEntity.builder().email("email@email.com").build())
                .build());
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.of(entity));
        when(jwtService.extractUsername(jwt)).thenReturn("anotherEmail@email.com");
        // then
        assertThrows(AccessDeniedException.class, () -> medicalRecordService.patchMedicalRecordForPatient(id, entity, jwt));
    }

    @Test
    public void testThatPatchMedicalForPatientUpdatesTheRecord() throws ParseException {
        // given
        Long id = 1L;
        String jwt = "jwt";
        MedicalRecordEntity entity = createMedicalRecordEntity(PatientEntity.builder().id(1L).build(), DoctorEntity
                .builder()
                .user(UserEntity.builder().email("email@email.com").build())
                .build());        MedicalRecordEntity updated = MedicalRecordEntity.builder().
            treatment("UPDATED")
        .build();
        MedicalRecordEntity expected = createMedicalRecordEntity(PatientEntity.builder().id(1L).build(), DoctorEntity
                .builder()
                .user(UserEntity.builder().email("email@email.com").build())
                .build());
        expected.setTreatment("UPDATED");
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.of(entity));
        when(jwtService.extractUsername(jwt)).thenReturn("email@email.com");
        when(medicalRecordRepository.save(expected)).thenReturn(expected);
        MedicalRecordEntity result = medicalRecordService.patchMedicalRecordForPatient(id, updated, jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected)
        );
        verify(messageProducer, times(1)).sendMedicalRecordCreateUpdate(any(MedicalRecordDocumentDto.class));
    }

    @Test
    public void testThatCreateMedicalRecordPerformsAsExpected() throws ParseException {
        // given
        String jwt = "jwt";
        PatientEntity patient = PatientEntity.builder().build();
        DoctorEntity doctor = DoctorEntity.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .build();
        MedicalRecordEntity entity = createMedicalRecordEntity(patient, null);
        // when
        when(medicalRecordRepository.save(entity)).thenReturn(entity);
        when(jwtService.extractUsername(jwt)).thenReturn("email");
        when(doctorRepository.findByUserEmail("email")).thenReturn(Optional.of(doctor));
        MedicalRecordEntity result = medicalRecordService.createMedicalRecord(entity, jwt);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, entity)
        );
        verify(messageProducer, times(1))
                .sendMedicalRecordCreateUpdate(any(MedicalRecordDocumentDto.class));
    }

    @Test
    public void testThatDeleteMedicalRecordByIdThrowsNotFoundException() {
        // given
        Long id = 1L;
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.empty());
        // then
        assertThrows(NotFoundException.class, () -> medicalRecordService.deleteById(1L, ""));
        verify(messageProducer, never()).sendMedicalRecordDelete(anyLong());
        verify(jwtService, never()).extractUsername(anyString());
        verify(medicalRecordRepository, never()).deleteById(anyLong());
    }

    @Test
    public void testThatDeleteMedicalRecordByIdThrowsNotAuthorizedException(){
        // given
        Long id = 1L;
        String jwt = "jwt";
        MedicalRecordEntity entity = createMedicalRecordEntity(null, DoctorEntity
                .builder()
                .user(UserEntity.builder().email("email@email.com")
                        .build()).build());
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.of(entity));
        when(jwtService.extractUsername(jwt)).thenReturn("incorrectEmail@email.com");
        // then
        assertThrows(AccessDeniedException.class, () -> medicalRecordService.deleteById(id, jwt));
        verify(medicalRecordRepository, never()).deleteById(anyLong());
        verify(messageProducer, never()).sendMedicalRecordDelete(anyLong());
    }

    @Test
    public void testThatDeleteMedicalRecordByIdPerformsAsExpected() {
        // given
        Long id = 1L;
        String jwt = "jwt";
        MedicalRecordEntity entity = createMedicalRecordEntity(null, DoctorEntity
                .builder()
                .user(UserEntity.builder().email("email@email.com")
                        .build()).build());
        // when
        when(medicalRecordRepository.findById(id)).thenReturn(Optional.of(entity));
        when(jwtService.extractUsername(jwt)).thenReturn("email@email.com");
        medicalRecordService.deleteById(id, jwt);
        // then
        verify(medicalRecordRepository, times(1)).deleteById(id);
        verify(messageProducer, times(1)).sendMedicalRecordDelete(id);
    }

    private MedicalRecordEntity createMedicalRecordEntity(PatientEntity patient, DoctorEntity doctor) {
        return MedicalRecordEntity.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .build();
    }

}
