package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.MedicalRecordListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.MedicalRecordListMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordListMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private MedicalRecordListMapper mapper;

    @Test
    public void testThatMapperMapsToDto() {
        // given
        MedicalRecordEntity entity = MedicalRecordEntity.builder().diagnosis("some diagnosis").build();
        MedicalRecordListDto expected = MedicalRecordListDto.builder().diagnosis("some diagnosis").build();
        // when
        when(modelMapper.map(entity, MedicalRecordListDto.class)).thenReturn(expected);
        MedicalRecordListDto result = mapper.toDto(entity);
        // then
        assertEquals(result, expected);
    }

}
