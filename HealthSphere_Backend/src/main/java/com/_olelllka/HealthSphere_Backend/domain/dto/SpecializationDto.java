package com._olelllka.HealthSphere_Backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SpecializationDto {
    private Long id;
    private String specializationName;
}
