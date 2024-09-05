package com._olelllka.HealthSphere_Backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="prescription_medicine")
public class PrescriptionMedicineEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="prescription_id", referencedColumnName = "id")
    private PrescriptionEntity prescription;
    @Column(nullable = false)
    private String medicineName;
    @Column(nullable = false)
    private String dosage;
    @Column(columnDefinition = "TEXT")
    private String instructions;
}
