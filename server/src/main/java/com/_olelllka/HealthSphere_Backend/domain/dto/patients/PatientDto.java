package com._olelllka.HealthSphere_Backend.domain.dto.patients;

import com._olelllka.HealthSphere_Backend.domain.dto.auth.UserDto;
import com._olelllka.HealthSphere_Backend.domain.entity.BloodType;
import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientDto implements Serializable {
    private Long id;
    private UserDto user;
    private String firstName;
    private String lastName;
    @Past(message = "Date of birth must be in the past.")
    private Date dateOfBirth;
    private Gender gender;
    private BloodType bloodType;
    private String allergies;
    private String address;
    private String phoneNumber;
    private Date createdAt;
    private Date updatedAt;
}
