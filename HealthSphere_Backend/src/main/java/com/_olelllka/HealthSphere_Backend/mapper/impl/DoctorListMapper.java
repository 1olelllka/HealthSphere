package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.DoctorListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DoctorListMapper implements Mapper<DoctorEntity, DoctorListDto> {

    private ModelMapper modelMapper;

    public DoctorListMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DoctorEntity toEntity(DoctorListDto doctorListDto) {
        return modelMapper.map(doctorListDto, DoctorEntity.class);
    }

    @Override
    public DoctorListDto toDto(DoctorEntity doctorEntity) {
        return modelMapper.map(doctorEntity, DoctorListDto.class);
    }
}
