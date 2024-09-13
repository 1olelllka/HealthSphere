package com._olelllka.HealthSphere_Backend.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "patients")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientDocument {
    @Id
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
}
