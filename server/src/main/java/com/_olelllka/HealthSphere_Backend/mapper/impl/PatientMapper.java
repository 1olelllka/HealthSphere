package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper implements Mapper<PatientEntity, PatientDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PatientEntity toEntity(PatientDto patientDto) {
        return modelMapper.map(patientDto, PatientEntity.class);
    }

    @Override
    public PatientDto toDto(PatientEntity patientEntity) {
        return modelMapper.map(patientEntity, PatientDto.class);
    }
}
