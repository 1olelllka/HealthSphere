package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.prescriptions.PrescriptionMedicineDto;
import com._olelllka.HealthSphere_Backend.domain.entity.PrescriptionMedicineEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.PrescriptionMedicineMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PrescriptionMedicineMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private PrescriptionMedicineMapper mapper;

    @Test
    public void testThatMedicineMapperMapsToDto() {
        // given
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder().id(1L).build();
        PrescriptionMedicineDto dto = PrescriptionMedicineDto.builder().id(1L).build();
        // when
        when(modelMapper.map(entity, PrescriptionMedicineDto.class)).thenReturn(dto);
        PrescriptionMedicineDto result = mapper.toDto(entity);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, dto)
        );
    }

    @Test
    public void testThatMedicineMapperMapsToEntity() {
        // given
        PrescriptionMedicineEntity entity = PrescriptionMedicineEntity.builder().id(1L).build();
        PrescriptionMedicineDto dto = PrescriptionMedicineDto.builder().id(1L).build();
        // when
        when(modelMapper.map(dto, PrescriptionMedicineEntity.class)).thenReturn(entity);
        PrescriptionMedicineEntity result = mapper.toEntity(dto);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, entity)
        );
    }

}
