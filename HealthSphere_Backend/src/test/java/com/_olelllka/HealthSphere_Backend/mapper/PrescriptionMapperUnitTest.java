package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PrescriptionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrescriptionMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PrescriptionMapper mapper;

    @Test
    public void testThatPrescriptionMapperMapsToDto() {
        // given
        PrescriptionEntity entity = PrescriptionEntity.builder().id(1L).build();
        PrescriptionDto dto = PrescriptionDto.builder().id(1L).build();
        // when
        when(modelMapper.map(entity, PrescriptionDto.class)).thenReturn(dto);
        PrescriptionDto result = mapper.toDto(entity);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, dto)
        );
    }

    @Test
    public void testThatPrescriptionMapperMapsToEntity() {
        // given
        PrescriptionEntity entity = PrescriptionEntity.builder().id(1L).build();
        PrescriptionDto dto = PrescriptionDto.builder().id(1L).build();
        // when
        when(modelMapper.map(dto, PrescriptionEntity.class)).thenReturn(entity);
        PrescriptionEntity result = mapper.toEntity(dto);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, entity)
        );
    }

}
