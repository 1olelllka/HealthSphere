package com._olelllka.HealthSphere_Backend.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name="specialization")
public class SpecializationEntity implements Serializable {

    @Id
    private Long id;
    @Column(nullable = false)
    private String specializationName;

}
