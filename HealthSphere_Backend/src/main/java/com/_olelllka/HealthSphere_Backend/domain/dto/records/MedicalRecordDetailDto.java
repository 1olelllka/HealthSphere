package com._olelllka.HealthSphere_Backend.domain.dto.records;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalRecordDetailDto {
    private Long id;
    private PatientDto patient;
    private DoctorDetailDto doctor;
    private LocalDate recordDate;
    @NotBlank(message = "Diagnosis must not be empty.")
    private String diagnosis;
    private String treatment;
    private PrescriptionEntity prescription;
    private Date createdAt;
    private Date updatedAt;
}
