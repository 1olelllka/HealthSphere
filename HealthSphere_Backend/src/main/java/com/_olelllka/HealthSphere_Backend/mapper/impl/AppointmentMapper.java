package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper implements Mapper<AppointmentEntity, AppointmentDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AppointmentEntity toEntity(AppointmentDto appointmentDto) {
        return modelMapper.map(appointmentDto, AppointmentEntity.class);
    }

    @Override
    public AppointmentDto toDto(AppointmentEntity appointmentEntity) {
        return modelMapper.map(appointmentEntity, AppointmentDto.class);
    }
}
