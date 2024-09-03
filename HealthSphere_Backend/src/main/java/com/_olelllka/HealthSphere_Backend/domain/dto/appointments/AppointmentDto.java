package com._olelllka.HealthSphere_Backend.domain.dto.appointments;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentDto {
    private Long id;
    private PatientEntity patient;
    private DoctorEntity doctor;
    @NotNull(message = "Appointment Date must not be null.")
    private Date appointmentDate;
    private Status status;
    private String reason;
    private Date createdAt;
    private Date updatedAt;
}
