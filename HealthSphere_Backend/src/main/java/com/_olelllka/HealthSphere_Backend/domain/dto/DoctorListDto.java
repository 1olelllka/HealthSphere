package com._olelllka.HealthSphere_Backend.domain.dto;

import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorListDto {
    private Long id;
    private UserDto user;
    private String firstName;
    private String lastName;
    private List<SpecializationDto> specializations;
    private Long experienceYears;
}