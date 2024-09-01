package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMapper implements Mapper<PrescriptionEntity, PrescriptionDto> {

    @Autowired
    private ModelMapper mapper;

    @Override
    public PrescriptionEntity toEntity(PrescriptionDto prescriptionDto) {
        return mapper.map(prescriptionDto, PrescriptionEntity.class);
    }

    @Override
    public PrescriptionDto toDto(PrescriptionEntity prescriptionEntity) {
        return mapper.map(prescriptionEntity, PrescriptionDto.class);
    }
}
