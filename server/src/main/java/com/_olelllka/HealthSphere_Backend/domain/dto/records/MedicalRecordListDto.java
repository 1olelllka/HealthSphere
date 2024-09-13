package com._olelllka.HealthSphere_Backend.domain.dto.records;

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
public class MedicalRecordListDto {
    private Long id;
    private DoctorRecordList doctor;
    private LocalDate recordDate;
    private String diagnosis;
    private String treatment;
}

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
class DoctorRecordList {
    private String id;
    private String firstName;
    private String lastName;
}