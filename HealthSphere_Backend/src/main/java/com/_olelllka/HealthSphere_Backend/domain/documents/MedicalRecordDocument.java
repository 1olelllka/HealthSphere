package com._olelllka.HealthSphere_Backend.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(indexName = "medical_records")
public class MedicalRecordDocument {
    @Id
    private Long id;
    private Long user_id;
    private String doctor;
    @Field(type= FieldType.Date, format = {}, pattern = "yyyy-MM-dd")
    private LocalDate recordDate;
    private String diagnosis;
    private String treatment;
}

