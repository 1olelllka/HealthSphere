package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RabbitMessageConsumerUnitTest {

    @Mock
    private DoctorElasticRepository repository;
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
                .build();
        DoctorDocument expected = DoctorDocument.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic Address")
                .experienceYears(5L)
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

}
