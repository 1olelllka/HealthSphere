package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordMessageProducerUnitTest {

    @Mock
    private RabbitTemplate amqpTemplate;
    @InjectMocks
    MedicalRecordMessageProducer messageProducer;

    @Test
    public void testThatSendMedicalRecordCreateUpdatePerformsAsExpected() {
        // given
        MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder().build();
        // when
        messageProducer.sendMedicalRecordCreateUpdate(dto);


        ArgumentCaptor<String> queueName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> exchangeName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MedicalRecordDocumentDto> medicalRecord = ArgumentCaptor.forClass(MedicalRecordDocumentDto.class);
        // then
        verify(amqpTemplate, times(1)).convertAndSend(
                exchangeName.capture(),
                queueName.capture(),
                medicalRecord.capture()
        );

        assertAll(
                () -> assertEquals(queueName.getValue(), "medical_record_create_update"),
                () -> assertEquals(exchangeName.getValue(), "record_exchange"),
                () -> assertEquals(medicalRecord.getValue(), dto)
        );
    }

    @Test
    public void testThatSendMedicalRecordDeletePerformsAsExpected() {
        // given
        Long id = 1L;
        // when
        messageProducer.sendMedicalRecordDelete(id);
        ArgumentCaptor<String> queueName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> exchangeName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> identifier = ArgumentCaptor.forClass(Long.class);
        // then
        verify(amqpTemplate, times(1)).convertAndSend(
                exchangeName.capture(),
                queueName.capture(),
                identifier.capture()
        );

        assertAll(
                () -> assertEquals(queueName.getValue(), "medical_record_delete"),
                () -> assertEquals(exchangeName.getValue(), "record_exchange"),
                () -> assertEquals(identifier.getValue(), id)
        );
    }

}
