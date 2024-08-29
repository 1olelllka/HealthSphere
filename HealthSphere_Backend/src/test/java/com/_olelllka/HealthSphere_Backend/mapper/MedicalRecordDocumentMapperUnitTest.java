package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.records.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.mapper.impl.MedicalRecordDocumentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordDocumentMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private MedicalRecordDocumentMapper mapper;

    @Test
    public void testThatMedicalRecordMapperMapsToDto() {
        // given
        MedicalRecordDocument document = MedicalRecordDocument.builder().build();
        MedicalRecordListDto expected = MedicalRecordListDto.builder().build();
        // when
        when(modelMapper.map(document, MedicalRecordListDto.class)).thenReturn(expected);
        MedicalRecordListDto result = mapper.toDto(document);
        // then
        assertEquals(expected, result);
    }
}
