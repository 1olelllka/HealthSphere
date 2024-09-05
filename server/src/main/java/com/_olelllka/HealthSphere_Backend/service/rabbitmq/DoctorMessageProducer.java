package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DoctorMessageProducer {

    private AmqpTemplate rabbitTemplate;

    public DoctorMessageProducer(RabbitTemplate rabbitTemplate) {
         this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDoctorToIndex(DoctorDocumentDto doctor) {
        rabbitTemplate.convertAndSend("doctor_exchange", "doctors_index_queue", doctor);
    }

    public void deleteDoctorFromIndex(Long id) {
        rabbitTemplate.convertAndSend("doctor_exchange", "doctor_index_delete_queue", id);
    }
}
