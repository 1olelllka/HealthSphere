package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionMedicineDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PrescriptionMedicineMapper implements Mapper<PrescriptionMedicineEntity, PrescriptionMedicineDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public PrescriptionMedicineEntity toEntity(PrescriptionMedicineDto prescriptionMedicineDto) {
        return modelMapper.map(prescriptionMedicineDto, PrescriptionMedicineEntity.class);
    }

    @Override
    public PrescriptionMedicineDto toDto(PrescriptionMedicineEntity prescriptionMedicineEntity) {
        return modelMapper.map(prescriptionMedicineEntity, PrescriptionMedicineDto.class);
    }
}
