package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorListDto;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoctorDocumentMapper implements Mapper<DoctorDocument, DoctorListDto> {

    private ModelMapper modelMapper;

    @Autowired
    public DoctorDocumentMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public DoctorDocument toEntity(DoctorListDto doctorListDto) { // will not be used
        return modelMapper.map(doctorListDto, DoctorDocument.class);
    }

    @Override
    public DoctorListDto toDto(DoctorDocument doctorDocument) {
        return modelMapper.map(doctorDocument, DoctorListDto.class);
    }
}
