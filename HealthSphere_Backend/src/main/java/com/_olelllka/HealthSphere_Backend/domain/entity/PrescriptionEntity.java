package com._olelllka.HealthSphere_Backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="prescription")
public class PrescriptionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    private PatientEntity patient;
    @ManyToOne
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;
    @CreationTimestamp
    @Column(updatable = false, name="issued_date")
    private Date issuedDate;
}
