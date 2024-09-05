package com._olelllka.HealthSphere_Backend.domain.dto.appointments;

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
public class UpdateAppointmentDto {
    @Future(message = "Appointment date must be in the future.")
    private Date appointmentDate;
    private Status status;
    private String reason;
}
