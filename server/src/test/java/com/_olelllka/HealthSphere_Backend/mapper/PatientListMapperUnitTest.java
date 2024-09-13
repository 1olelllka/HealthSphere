package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.patients.PatientListDto;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientListMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PatientListMapperUnitTest {
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PatientListMapper mapper;

    @Test
    public void testThatToDocumentWorksWell() {
        // given
        PatientListDto patientDto = PatientListDto.builder().firstName("Something").build();
        PatientDocument patientDocument = PatientDocument.builder().firstName("Something").build();
        // when
        when(modelMapper.map(patientDto, PatientDocument.class)).thenReturn(patientDocument);
        PatientDocument result = mapper.toEntity(patientDto);
        // then
        assertEquals(result, patientDocument);
        verify(modelMapper, times(1)).map(patientDto, PatientDocument.class);
    }

    @Test
    public void testThatToDtoWorksWell() {
        // given
        PatientListDto patientDto = PatientListDto.builder().firstName("Something").build();
        PatientDocument patientDocument = PatientDocument.builder().firstName("Something").build();
        // when
        when(modelMapper.map(patientDocument, PatientListDto.class)).thenReturn(patientDto);
        PatientListDto result = mapper.toDto(patientDocument);
        // then
        assertEquals(result, patientDto);
        verify(modelMapper, times(1)).map(patientDocument, PatientListDto.class);
    }
}
