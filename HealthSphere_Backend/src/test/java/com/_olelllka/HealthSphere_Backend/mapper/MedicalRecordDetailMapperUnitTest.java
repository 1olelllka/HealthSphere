package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.MedicalRecordDetailDto;
import com._olelllka.HealthSphere_Backend.domain.entity.MedicalRecordEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.MedicalRecordDetailMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MedicalRecordDetailMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private MedicalRecordDetailMapper mapper;

    @Test
    public void testThatMapperMapsToDto() {
        // given
        MedicalRecordEntity entity = MedicalRecordEntity.builder().diagnosis("some diagnosis").build();
        MedicalRecordDetailDto expected = MedicalRecordDetailDto.builder().diagnosis("some diagnosis").build();
        // when
        when(modelMapper.map(entity, MedicalRecordDetailDto.class)).thenReturn(expected);
        MedicalRecordDetailDto result = mapper.toDto(entity);

        assertEquals(result, expected);

    }
}
