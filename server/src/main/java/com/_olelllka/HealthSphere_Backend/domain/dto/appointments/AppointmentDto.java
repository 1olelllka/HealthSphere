package com._olelllka.HealthSphere_Backend.domain.dto.appointments;

import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.domain.entity.Status;
import jakarta.validation.constraints.Future;
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
    @NotNull(message = "Appointment date must not be null.")
    @Future(message = "Appointment date must be in the future.")
    private Date appointmentDate;
    @NotNull(message = "Status must not be null.")
    private Status status;
    private String reason;
    private Date createdAt;
    private Date updatedAt;
}
