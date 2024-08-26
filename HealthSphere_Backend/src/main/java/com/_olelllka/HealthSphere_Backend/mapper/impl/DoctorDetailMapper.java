package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class DoctorDetailMapper implements Mapper<DoctorEntity, DoctorDetailDto> {

    private ModelMapper modelMapper;

    public DoctorDetailMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DoctorEntity toEntity(DoctorDetailDto doctorDetailDto) {
        return modelMapper.map(doctorDetailDto, DoctorEntity.class);
    }

    @Override
    public DoctorDetailDto toDto(DoctorEntity doctorEntity) {
        return modelMapper.map(doctorEntity, DoctorDetailDto.class);
    }
}
