package com._olelllka.HealthSphere_Backend.service.impl;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.repositories.DoctorRepository;
import com._olelllka.HealthSphere_Backend.repositories.PrescriptionMedicineRepository;
import com._olelllka.HealthSphere_Backend.repositories.PrescriptionRepository;
import com._olelllka.HealthSphere_Backend.rest.exceptions.NotFoundException;
import com._olelllka.HealthSphere_Backend.service.JwtService;
import com._olelllka.HealthSphere_Backend.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private PrescriptionRepository repository;
    private JwtService jwtService;
    private DoctorRepository doctorRepository;
    private PrescriptionMedicineRepository medicineRepository;

    @Autowired
    public PrescriptionServiceImpl(PrescriptionRepository repository,
                                   DoctorRepository doctorRepository,
                                   PrescriptionMedicineRepository medicineRepository,
                                   JwtService jwtService) {
        this.repository = repository;
        this.doctorRepository = doctorRepository;
        this.jwtService = jwtService;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public PrescriptionEntity createPrescription(PrescriptionEntity entity, String jwt) {
        String email = jwtService.extractUsername(jwt);
        DoctorEntity doctor = doctorRepository
                .findByUserEmail(email).orElseThrow(() -> new NotFoundException("Doctor with such email was not found."));
        entity.setDoctor(doctor);
        return repository.save(entity);
    }

    @Override
    public PrescriptionMedicineEntity addMedicineToPrescription(Long id, PrescriptionMedicineEntity entity) {
        PrescriptionEntity prescription = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prescription with such id was not found."));
        entity.setPrescription(prescription);
        return medicineRepository.save(entity);
    }

    @Override
    public List<PrescriptionMedicineEntity> getAllMedicineToPrescription(Long id) {
        return medicineRepository.findByPrescriptionId(id);
    }

    @Override
    public void removeMedicineById(Long id) {
        medicineRepository.deleteById(id);
    }

    @Override
    public PrescriptionMedicineEntity updateTheMedicineById(Long id, PrescriptionMedicineEntity entity) {
        return medicineRepository.findById(id).map(med -> {
            Optional.ofNullable(entity.getMedicineName()).ifPresent(med::setMedicineName);
            Optional.ofNullable(entity.getDosage()).ifPresent(med::setDosage);
            Optional.ofNullable(entity.getInstructions()).ifPresent(med::setInstructions);
            return medicineRepository.save(med);
        }).orElseThrow(() -> new NotFoundException("Medicine with such id was not found."));
    }

    @Override
    @Transactional
    public void deletePrescription(Long id) {
        List<PrescriptionMedicineEntity> medicines = medicineRepository.findByPrescriptionId(id);
        medicineRepository.deleteAll(medicines);
        repository.deleteById(id);
    }
}
