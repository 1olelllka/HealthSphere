package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.PatientDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PatientEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PatientMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PatientMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PatientMapper mapper;

    @Test
    public void testThatToEntityWorksWell() {
        // given
        PatientDto patientDto = PatientDto.builder().firstName("Something").build();
        PatientEntity patientEntity = PatientEntity.builder().firstName("Something").build();
        // when
        when(modelMapper.map(patientDto, PatientEntity.class)).thenReturn(patientEntity);
        PatientEntity result = mapper.toEntity(patientDto);
        // then
        assertEquals(result, patientEntity);
        verify(modelMapper, times(1)).map(patientDto, PatientEntity.class);
    }

    @Test
    public void testThatToDtoWorksWell() {
        // given
        PatientDto patientDto = PatientDto.builder().firstName("Something").build();
        PatientEntity patientEntity = PatientEntity.builder().firstName("Something").build();
        // when
        when(modelMapper.map(patientEntity, PatientDto.class)).thenReturn(patientDto);
        PatientDto result = mapper.toDto(patientEntity);
        // then
        assertEquals(result, patientDto);
        verify(modelMapper, times(1)).map(patientEntity, PatientDto.class);
    }

}
