package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordDetailMapper implements Mapper<MedicalRecordEntity, MedicalRecordDetailDto> {

    private ModelMapper modelMapper;

    @Autowired
    public MedicalRecordDetailMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public MedicalRecordEntity toEntity(MedicalRecordDetailDto medicalRecordDetailDto) {
        return modelMapper.map(medicalRecordDetailDto, MedicalRecordEntity.class);
    }

    @Override
    public MedicalRecordDetailDto toDto(MedicalRecordEntity medicalRecordEntity) {
        return modelMapper.map(medicalRecordEntity, MedicalRecordDetailDto.class);
    }
}
