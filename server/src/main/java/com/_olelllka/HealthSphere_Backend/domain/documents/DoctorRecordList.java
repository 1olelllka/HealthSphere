package com._olelllka.HealthSphere_Backend.domain.documents;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorRecordList {
    private Long id;
    private String firstName;
    private String lastName;
}
