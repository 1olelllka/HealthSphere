package com._olelllka.HealthSphere_Backend.domain.dto.doctors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecializationDto {
    private Long id;
    private String specializationName;
}
