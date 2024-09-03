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
public class AppointmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    private PatientEntity patient;
    @ManyToOne
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;
    @Column(nullable = false, unique = true)
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
