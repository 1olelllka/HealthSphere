package com._olelllka.HealthSphere_Backend.domain.dto.doctors;

import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorDetailDto implements Serializable {
    private Long id;
    private UserDto user;
    @NotBlank(message = "First name must not be empty.")
    private String firstName;
    @NotBlank(message = "Last name must not be empty.")
    private String lastName;
    private List<SpecializationDto> specializations;
    @NotBlank(message = "License number must not be empty.")
    private String licenseNumber;
    private Long experienceYears;
    private Gender gender;
    @NotBlank(message = "Phone number must not be empty.")
    private String phoneNumber;
    @NotBlank(message = "Clinic address must not be empty.")
    private String clinicAddress;
    private Date createdAt;
    private Date updatedAt;
}
