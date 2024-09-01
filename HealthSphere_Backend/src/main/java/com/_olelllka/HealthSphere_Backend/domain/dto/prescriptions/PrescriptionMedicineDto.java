package com._olelllka.HealthSphere_Backend.domain.dto.prescriptions;

import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PrescriptionMedicineDto {
    private Long id;
    @JsonIgnore
    private PrescriptionEntity prescription;
    @NotBlank(message = "Medicine name must not be empty.")
    private String medicineName;
    @NotBlank(message = "Dosage must not be empty.")
    private String dosage;
    private String instructions;
}
