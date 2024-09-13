package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PatientMessageProducerUnitTest {

    @Mock
    private RabbitTemplate amqpTemplate;
    @InjectMocks
    PatientMessageProducer messageProducer;

    @Test
    public void testThatSendMedicalRecordCreateUpdatePerformsAsExpected() {
        // given
        PatientListDto dto = PatientListDto.builder().build();
        // when
        messageProducer.sendMessageCreatePatient(dto);


        ArgumentCaptor<String> queueName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> exchangeName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<PatientListDto> patientDto = ArgumentCaptor.forClass(PatientListDto.class);
        // then
        verify(amqpTemplate, times(1)).convertAndSend(
                exchangeName.capture(),
                queueName.capture(),
                patientDto.capture()
        );

        assertAll(
                () -> assertEquals(queueName.getValue(), "patient_index_queue"),
                () -> assertEquals(exchangeName.getValue(), "patient_exchange"),
                () -> assertEquals(patientDto.getValue(), dto)
        );
    }

    @Test
    public void testThatSendMedicalRecordDeletePerformsAsExpected() {
        // given
        Long id = 1L;
        // when
        messageProducer.sendMessageDeletePatient(id);
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
                () -> assertEquals(queueName.getValue(), "patient_index_delete_queue"),
                () -> assertEquals(exchangeName.getValue(), "patient_exchange"),
                () -> assertEquals(identifier.getValue(), id)
        );
    }

}
