package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.SpecializationMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpecializationMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private SpecializationMapper mapper;

    @Test
    public void testThatSpecializationMapperMapsToDto() {
        // given
        SpecializationEntity entity = SpecializationEntity.builder().id(1L).build();
        SpecializationDto expected = SpecializationDto.builder().id(1L).build();
        // when
        when(modelMapper.map(entity, SpecializationDto.class)).thenReturn(expected);
        SpecializationDto result = mapper.toDto(entity);
        // then
        assertEquals(expected, result);
    }

    @Test
    public void testThatSpecializationMapperMapsToEntity() {
        // given
        SpecializationEntity expected = SpecializationEntity.builder().id(1L).build();
        SpecializationDto dto = SpecializationDto.builder().id(1L).build();
        // when
        when(modelMapper.map(dto, SpecializationEntity.class)).thenReturn(expected);
        SpecializationEntity result = mapper.toEntity(dto);
        // then
        assertEquals(expected, result);
    }
}
