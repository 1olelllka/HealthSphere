package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DoctorMessageProducerUnitTest {

    @Mock
    private AmqpTemplate amqpTemplate;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @InjectMocks
    private DoctorMessageProducer messageProducer;

    @Test
    public void testThatSendDoctorToIndexAcceptsCorrectData() {
        // given
        DoctorDocumentDto dto = DoctorDocumentDto.builder().build();
        // when
        messageProducer.sendDoctorToIndex(dto);

        ArgumentCaptor<String> queueName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> exchangeName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> data = ArgumentCaptor.forClass(Object.class);

        verify(rabbitTemplate, times(1)).convertAndSend(
                exchangeName.capture(),
                queueName.capture(),
                data.capture()
        );

        assertAll(
                () -> assertEquals(exchangeName.getValue(), "doctor_exchange"),
                () -> assertEquals(queueName.getValue(), "doctors_index_queue"),
                () -> assertEquals(data.getValue(), dto)
        );
    }

    @Test
    public void testThatDeleteDoctorFromIndexAcceptsCorrectData() {
        // given
        Long id = 1L;
        // when
        messageProducer.deleteDoctorFromIndex(id);

        ArgumentCaptor<String> queueName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> exchangeName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> identity = ArgumentCaptor.forClass(Long.class);

        verify(rabbitTemplate, times(1)).convertAndSend(
                exchangeName.capture(),
                queueName.capture(),
                identity.capture()
        );
        assertAll(
                () -> assertEquals(exchangeName.getValue(), "doctor_exchange"),
                () -> assertEquals(queueName.getValue(), "doctor_index_delete_queue"),
                () -> assertEquals(identity.getValue(), id)
        );
    }

}
