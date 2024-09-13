package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PatientMessageProducer {

    private AmqpTemplate amqpTemplate;

    public PatientMessageProducer (RabbitTemplate template) {
        this.amqpTemplate = template;
    }

    public void sendMessageCreatePatient(PatientListDto dto) {
        amqpTemplate.convertAndSend("patient_exchange", "patient_index_queue",dto);
    }

    public void sendMessageDeletePatient(Long id) {
        amqpTemplate.convertAndSend("patient_exchange", "patient_index_delete_queue", id);
    }
}
