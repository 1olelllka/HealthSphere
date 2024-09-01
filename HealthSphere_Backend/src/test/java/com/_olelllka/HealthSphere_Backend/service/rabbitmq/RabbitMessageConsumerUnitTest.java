package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.documents.DoctorRecordList;
import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordElasticRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RabbitMessageConsumerUnitTest {

    @Mock
    private DoctorElasticRepository repository;
    @Mock
    private MedicalRecordElasticRepository medicalRecordRepository;
    @InjectMocks
    private RabbitMessageConsumer messageConsumer;

    @Test
    public void testThatConsumeDoctorForIndexingGetsCorrectArgs() {
        // given
        DoctorDocumentDto dto = DoctorDocumentDto.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic Address")
                .experienceYears(5L)
                .specializations(List.of())
                .build();
        DoctorDocument expected = DoctorDocument.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic Address")
                .experienceYears(5L)
                .specializations(List.of())
                .build();
        // when
        messageConsumer.consumeDoctorForIndexing(dto);
        // then
        verify(repository, times(1)).save(expected);
    }

    @Test
    public void testThatConsumeDoctorDeleteGetsCorrectArgs() {
        // given
        Long id = 1L;
        // when
        messageConsumer.consumeDoctorDelete(id);
        // then
        verify(repository, times(1)).deleteById(id);
    }

    @Test
    public void testThatConsumeMedicalRecordCreateUpdateGetsCorrectArgs() {
        // given
        MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder()
                .id(1L)
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .user_id(1L)
                .treatment("TREATMENT")
                .diagnosis("DIAGNOSIS")
                .doctor(DoctorRecordList.builder().build())
                .build();
        MedicalRecordDocument expected = MedicalRecordDocument.builder()
                .id(1L)
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .user_id(1L)
                .treatment("TREATMENT")
                .diagnosis("DIAGNOSIS")
                .doctor(DoctorRecordList.builder().build())
                .build();
        // when
        messageConsumer.consumeMedicalRecordCreateUpdate(dto);
        // then
        verify(medicalRecordRepository, times(1)).save(expected);
    }

}
