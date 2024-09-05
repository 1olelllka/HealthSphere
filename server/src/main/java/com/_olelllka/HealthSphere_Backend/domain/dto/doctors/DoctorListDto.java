package com._olelllka.HealthSphere_Backend.domain.dto.doctors;

import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private UserDto user;
    private String firstName;
    private String lastName;
    private String clinicAddress;
    private List<SpecializationDto> specializations;
    private Long experienceYears;
}