package com._olelllka.HealthSphere_Backend.domain.dto.doctors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorDocumentDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String clinicAddress;
    private List<SpecializationDto> specializations;
    private Long experienceYears;
}
