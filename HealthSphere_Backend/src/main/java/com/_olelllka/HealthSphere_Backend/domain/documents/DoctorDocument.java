package com._olelllka.HealthSphere_Backend.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "doctors")
public class DoctorDocument {
    @Id
    private Long id;
    private String firstName;
    private String lastName;
    private String clinicAddress;
    private Long experienceYears;
    private List<String> specializations;
}
