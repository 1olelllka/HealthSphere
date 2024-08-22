package com._olelllka.HealthSphere_Backend.domain.dto;

import com._olelllka.HealthSphere_Backend.domain.entity.Gender;
import com._olelllka.HealthSphere_Backend.domain.entity.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientDto {
    private Long id;
    private UserEntity user;
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private Gender gender;
    private String address;
    private String phoneNumber;
    private Date createdAt;
    private Date updatedAt;
}
