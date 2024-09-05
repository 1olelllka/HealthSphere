package com._olelllka.HealthSphere_Backend.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="doctor")
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "_user_id", referencedColumnName = "id")
    private UserEntity user;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @ManyToMany
    @JoinTable(
            name="doctor_specialization",
            joinColumns = @JoinColumn(name="doctor_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name="specialization_id", referencedColumnName = "id")
    )
    private List<SpecializationEntity> specializations;
    @Column(nullable = false)
    private String licenseNumber;
    private Long experienceYears;
    private String phoneNumber;
    private String clinicAddress;
    @CreationTimestamp
    @Column(updatable = false, name="created_at")
    private Date createdAt;
    @UpdateTimestamp
    @Column(name="updated_at")
    private Date updatedAt;

}
