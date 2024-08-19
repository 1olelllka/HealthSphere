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
@Table(name="record")
public class MedicalRecordEntity {
    @Id
    private Long id;
    @OneToOne
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    private PatientEntity patient;
    @OneToOne
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;
    @Column(nullable = false)
    private Date recordDate;
    @Column(nullable = false)
    private String diagnosis;
    @Column(columnDefinition = "TEXT")
    private String treatment;
    @OneToOne
    @JoinColumn(name="prescription_id", referencedColumnName = "id")
    private PrescriptionEntity prescription;
    @CreationTimestamp
    @Column(updatable = false, name="created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;
}
