package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordListMapper implements Mapper<MedicalRecordEntity, MedicalRecordListDto> {

    private ModelMapper modelMapper;

    @Autowired
    public MedicalRecordListMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MedicalRecordEntity toEntity(MedicalRecordListDto medicalRecordListDto) {
        return modelMapper.map(medicalRecordListDto, MedicalRecordEntity.class);
    }

    @Override
    public MedicalRecordListDto toDto(MedicalRecordEntity medicalRecordEntity) {
        return modelMapper.map(medicalRecordEntity, MedicalRecordListDto.class);
    }
}
