package com._olelllka.HealthSphere_Backend.domain.dto.patients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientListDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
