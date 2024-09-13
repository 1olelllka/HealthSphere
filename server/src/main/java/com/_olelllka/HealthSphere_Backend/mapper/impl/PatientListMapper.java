package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientListMapper implements Mapper<PatientDocument, PatientListDto> {

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public PatientDocument toEntity(PatientListDto patientListDto) {
        return modelMapper.map(patientListDto, PatientDocument.class);
    }

    @Override
    public PatientListDto toDto(PatientDocument patientDocument) {
        return modelMapper.map(patientDocument, PatientListDto.class);
    }
}
