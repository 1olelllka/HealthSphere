package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDetailDto;
import com._olelllka.HealthSphere_Backend.domain.entity.DoctorEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDetailMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorDetailMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DoctorDetailMapper mapper;

    @Test
    public void testThatDoctorDetailMapperMapsToDto() {
        // given
        DoctorEntity doctor = DoctorEntity.builder().firstName("First Name").build();
        DoctorDetailDto expected = DoctorDetailDto.builder().firstName("First Name").build();
        // when
        when(modelMapper.map(doctor, DoctorDetailDto.class)).thenReturn(expected);
        DoctorDetailDto result = mapper.toDto(doctor);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected)
        );
        verify(modelMapper, times(1)).map(doctor, DoctorDetailDto.class);
    }

}
