package com._olelllka.HealthSphere_Backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="record", uniqueConstraints = {@UniqueConstraint(name="doctor_patient_recordDate_constraint",
        columnNames = {"doctor_id", "patient_id", "recordDate"})})
public class MedicalRecordEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="patient_id", referencedColumnName = "id")
    private PatientEntity patient;
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name="doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;
    @Column(nullable = false)
    private LocalDate recordDate;
    @Column(nullable = false)
    private String diagnosis;
    @Column(columnDefinition = "TEXT")
    private String treatment;
    @OneToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name="prescription_id", referencedColumnName = "id")
    private PrescriptionEntity prescription;
    @CreationTimestamp
    @Column(updatable = false, name="created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;
}
