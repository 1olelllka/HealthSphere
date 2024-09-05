package com._olelllka.HealthSphere_Backend.domain.dto.prescriptions;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrescriptionDto {
    private Long id;
    @NotNull(message = "Prescription must have patient issued to.")
    private PatientEntity patient;
    private DoctorEntity doctor;
    private Date issuedDate;
}
