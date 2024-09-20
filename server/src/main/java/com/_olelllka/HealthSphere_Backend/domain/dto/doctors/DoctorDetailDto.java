package com._olelllka.HealthSphere_Backend.domain.dto.doctors;

import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorDetailDto {
    private Long id;
    private UserDto user;
    private String firstName;
    private String lastName;
    private List<SpecializationDto> specializations;
    private String licenseNumber;
    private Long experienceYears;
    private Gender gender;
    private String phoneNumber;
    private String clinicAddress;
    private Date createdAt;
    private Date updatedAt;
}
