package com._olelllka.HealthSphere_Backend.domain.dto.records;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorRecordList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MedicalRecordDocumentDto {
    private Long id;
    private Long user_id;
    private DoctorRecordList doctor;
    private LocalDate recordDate;
    private String diagnosis;
    private String treatment;
}
