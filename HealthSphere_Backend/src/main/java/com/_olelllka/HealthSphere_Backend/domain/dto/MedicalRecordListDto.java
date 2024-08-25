package com._olelllka.HealthSphere_Backend.domain.dto;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalRecordListDto {
    private Long id;
    private DoctorEntity doctor;
    private Date recordDate;
    private String diagnosis;
    private String treatment;
}
