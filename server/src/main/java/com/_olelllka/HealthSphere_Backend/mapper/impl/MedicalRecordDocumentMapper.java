package com._olelllka.HealthSphere_Backend.mapper.impl;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.mapper.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MedicalRecordDocumentMapper implements Mapper<MedicalRecordDocument, MedicalRecordListDto> {

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public MedicalRecordDocument toEntity(MedicalRecordListDto medicalRecordListDto) {
        return modelMapper.map(medicalRecordListDto, MedicalRecordDocument.class);
    }

    @Override
    public MedicalRecordListDto toDto(MedicalRecordDocument medicalRecordDocument) {
        return modelMapper.map(medicalRecordDocument, MedicalRecordListDto.class);
    }
}
