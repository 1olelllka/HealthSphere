package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.DoctorListDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorListMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorListMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DoctorListMapper mapper;

    @Test
    public void testThatMapperConvertsEntityToDto() {
        // given
        DoctorEntity doctor = DoctorEntity.builder()
                .firstName("First Name").build();
        DoctorListDto expected = DoctorListDto.builder().firstName("First Name").build();
        // when
        when(modelMapper.map(doctor, DoctorListDto.class)).thenReturn(expected);
        DoctorListDto result = mapper.toDto(doctor);
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected)
        );
        verify(modelMapper, times(1)).map(doctor, DoctorListDto.class);
    }

}
