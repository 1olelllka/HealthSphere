package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordMessageProducer {

    private AmqpTemplate amqpTemplate;

    public MedicalRecordMessageProducer(RabbitTemplate rabbitTemplate) {
        this.amqpTemplate = rabbitTemplate;
    }

    public void sendMedicalRecordCreateUpdate(MedicalRecordDocumentDto dto) {
        amqpTemplate.convertAndSend("record_exchange",  "medical_record_create_update", dto);
    }

    public void sendMedicalRecordDelete(Long id) {
        amqpTemplate.convertAndSend("record_exchange", "medical_record_delete", id);
    }
}
