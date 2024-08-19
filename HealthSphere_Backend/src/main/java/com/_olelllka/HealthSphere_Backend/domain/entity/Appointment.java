package com._olelllka.HealthSphere_Backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="appointment")
public class Appointment {
    @Id
    private Long id;
    @OneToOne
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    private Patient patient;
    @OneToOne
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private Doctor doctor;
    @Column(nullable = false)
    private Date appointmentDate;
    @Enumerated(EnumType.STRING)
    private Status status;
    private String reason;
    @CreationTimestamp
    @Column(updatable = false, name="created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;
}
