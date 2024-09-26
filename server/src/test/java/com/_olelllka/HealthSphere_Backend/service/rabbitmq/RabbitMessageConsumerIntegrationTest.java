package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDocumentDto;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.MedicalRecordElasticRepository;
import com._olelllka.HealthSphere_Backend.repositories.PatientElasticRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RabbitMessageConsumerIntegrationTest extends AbstractTestContainers {

    private DoctorElasticRepository doctorElasticRepository;
    private MedicalRecordElasticRepository medicalRecordElasticRepository;
    private PatientElasticRepository patientElasticRepository;
    private DoctorMessageProducer doctorMessageProducer;
    private MedicalRecordMessageProducer recordMessageProducer;
    private PatientMessageProducer patientMessageProducer;
    private RabbitListenerEndpointRegistry listenerRegistry;
    private RabbitAdmin admin;

    @Autowired
    public RabbitMessageConsumerIntegrationTest(DoctorElasticRepository doctorElasticRepository,
                                                MedicalRecordElasticRepository medicalRecordElasticRepository,
                                                PatientElasticRepository patientElasticRepository,
                                                DoctorMessageProducer doctorMessageProducer,
                                                MedicalRecordMessageProducer recordMessageProducer,
                                                RabbitListenerEndpointRegistry listenerRegistry,
                                                PatientMessageProducer patientMessageProducer,
                                                RabbitAdmin admin) {
        this.doctorElasticRepository = doctorElasticRepository;
        this.patientElasticRepository = patientElasticRepository;
        this.medicalRecordElasticRepository = medicalRecordElasticRepository;
        this.doctorMessageProducer = doctorMessageProducer;
        this.recordMessageProducer = recordMessageProducer;
        this.patientMessageProducer = patientMessageProducer;
        this.listenerRegistry = listenerRegistry;
        this.admin = admin;
    }

    @Test
    public void testThatConsumeDoctorAddPerformsAddToElasticSearchDb() {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctors_index_queue")).getMessageCount());
        SpecializationDto dto = SpecializationDto.builder().id(1L).specializationName("NAME").build();
        DoctorDocumentDto doctorDocument = DoctorDocumentDto
                .builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic")
                .experienceYears(5L)
                .specializations(List.of(dto))
                .build();
        listenerRegistry.getListenerContainer("doctor.post").start();
        doctorMessageProducer.sendDoctorToIndex(doctorDocument);
        awaitFunction("doctors_index_queue");
        Optional<DoctorDocument> result = doctorElasticRepository.findById(1L);
        assertTrue(result.isPresent());

        // tearDown
        doctorElasticRepository.deleteById(1L);
    }

    @Test
    public void testThatConsumeDoctorDeletePerformsDeleteFromElasticSearchDb() {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount());
        DoctorDocument doctorDocument = DoctorDocument
                .builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic")
                .experienceYears(5L)
                .build();
        doctorElasticRepository.save(doctorDocument);
        listenerRegistry.getListenerContainer("doctor.delete").start();
        doctorMessageProducer.deleteDoctorFromIndex(1L);

        awaitFunction("doctor_index_delete_queue");
        Optional<DoctorDocument> result = doctorElasticRepository.findById(1L);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testThatConsumeMedicalRecordCreateUpdatePerformsAddToElasticSearchDb() {
        listenerRegistry.stop();
        MedicalRecordDocumentDto dto = MedicalRecordDocumentDto.builder()
                .id(10L)
                .recordDate(LocalDate.of(2020, Month.APRIL, 1))
                .diagnosis("Diagnosis")
                .treatment("Treatment")
                .user_id(1L)
                .build();
        listenerRegistry.getListenerContainer("medical-record.post").start();
        recordMessageProducer.sendMedicalRecordCreateUpdate(dto);
        awaitFunction("medical_record_create_update");
        assertTrue(medicalRecordElasticRepository.existsById(10L));
    }

    @Test
    public void testThatConsumeMedicalRecordDeletePerformsDeleteFromElasticSearchDb() {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("medical_record_delete")).getMessageCount());
        MedicalRecordDocument document = MedicalRecordDocument.builder().id(1L).build();
        medicalRecordElasticRepository.save(document);
        listenerRegistry.getListenerContainer("medical-record.delete").start();
        recordMessageProducer.sendMedicalRecordDelete(1L);
        awaitFunction("medical_record_delete");
        assertFalse(medicalRecordElasticRepository.existsById(1L));
    }

    @Test
    public void testThatConsumePatientCreateUpdatePerformsAddToElasticSearchDb() {
        listenerRegistry.stop();
        PatientListDto patientListDto = PatientListDto.builder()
                        .id(1L)
                        .email("email@email.com")
                        .firstName("First Name")
                        .lastName("Last Name")
                        .build();
        listenerRegistry.getListenerContainer("patient.post").start();
        patientMessageProducer.sendMessageCreatePatient(patientListDto);
        awaitFunction("patient_index_queue");
        assertTrue(medicalRecordElasticRepository.existsById(10L));
    }

    @Test
    public void testThatConsumePatientDeletePerformsDeleteFromElasticSearchDb() {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("patient_index_delete_queue")).getMessageCount());
        PatientDocument document = PatientDocument.builder().id(1L).build();
        PatientDocument res = patientElasticRepository.save(document);
        listenerRegistry.getListenerContainer("patient.delete").start();
        patientMessageProducer.sendMessageDeletePatient(res.getId());
        awaitFunction("patient_index_delete_queue");
        assertFalse(patientElasticRepository.existsById(res.getId()));
    }

    private void awaitFunction(String queueName) {
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo(queueName).getMessageCount() == 0, Boolean.TRUE::equals);
    }
}
