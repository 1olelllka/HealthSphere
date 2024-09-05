package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpecializationMapper implements Mapper<SpecializationEntity, SpecializationDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public SpecializationEntity toEntity(SpecializationDto specializationDto) {
        return modelMapper.map(specializationDto, SpecializationEntity.class);
    }

    @Override
    public SpecializationDto toDto(SpecializationEntity specializationEntity) {
        return modelMapper.map(specializationEntity, SpecializationDto.class);
    }
}
