package com._olelllka.HealthSphere_Backend.service.rabbitmq;

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
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMessageConsumer {

    @Autowired
    private DoctorElasticRepository repository;

    @Autowired
    private MedicalRecordElasticRepository recordRepository;

    @Autowired
    private PatientElasticRepository patientRepository;


    @RabbitListener(id = "doctor.post", queues = "doctors_index_queue")
    public void consumeDoctorForIndexing(DoctorDocumentDto doctor) {
        DoctorDocument doctorDocument = DoctorDocument.builder()
                .id(doctor.getId())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .clinicAddress(doctor.getClinicAddress())
                .experienceYears(doctor.getExperienceYears())
                .specializations(doctor.getSpecializations().stream().map(SpecializationDto::getSpecializationName).toList())
                .build();

        repository.save(doctorDocument);
    }

    @RabbitListener(id = "doctor.delete", queues = "doctor_index_delete_queue")
    public void consumeDoctorDelete(Long id) {
        repository.deleteById(id);
    }

    @RabbitListener(id="medical-record.post", queues = "medical_record_create_update")
    public void consumeMedicalRecordCreateUpdate(MedicalRecordDocumentDto dto) {
        MedicalRecordDocument document = MedicalRecordDocument.builder()
                .id(dto.getId())
                .user_id(dto.getUser_id())
                .diagnosis(dto.getDiagnosis())
                .recordDate(dto.getRecordDate())
                .treatment(dto.getTreatment())
                .doctor(dto.getDoctor())
                .build();
        recordRepository.save(document);
    }

    @RabbitListener(id="medical-record.delete", queues = "medical_record_delete")
    public void consumeMedicalRecordDelete(Long id) {
        recordRepository.deleteById(id);
    }

    @RabbitListener(id="patient.post", queues = "patient_index_queue")
    public void consumePatientCreateUpdate(PatientListDto dto) {
        PatientDocument document = PatientDocument.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .id(dto.getId())
                .build();
        patientRepository.save(document);
    }

    @RabbitListener(id="patient.delete", queues = "patient_index_delete_queue")
    public void consumePatientDelete(Long id) {
        patientRepository.deleteById(id);
    }
}
