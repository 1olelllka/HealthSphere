package com._olelllka.HealthSphere_Backend.domain.dto;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalRecordDetailDto {
    private Long id;
    private PatientEntity patient;
    private DoctorEntity doctor;
    private Date recordDate;
    private String diagnosis;
    private String treatment;
    private PrescriptionEntity prescription;
    private Date createdAt;
    private Date updatedAt;
}
