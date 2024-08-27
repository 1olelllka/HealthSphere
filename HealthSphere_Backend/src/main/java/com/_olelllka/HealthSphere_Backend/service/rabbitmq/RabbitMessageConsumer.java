package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMessageConsumer {

    @Autowired
    private DoctorElasticRepository repository;


    @RabbitListener(id = "doctor.post", queues = "doctors_index_queue")
    public void consumeDoctorForIndexing(DoctorDocumentDto doctor) {
        DoctorDocument doctorDocument = DoctorDocument.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .clinicAddress(doctor.getClinicAddress())
                .experienceYears(doctor.getExperienceYears())
                .build();

        repository.save(doctorDocument);
    }

    @RabbitListener(id = "doctor.delete", queues = "doctor_index_delete_queue")
    public void consumeDoctorDelete(Long id) {
        repository.deleteById(id);
    }
}
