package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.dto.appointments.AppointmentDto;
import com._olelllka.HealthSphere_Backend.domain.entity.AppointmentEntity;
import com._olelllka.HealthSphere_Backend.mapper.impl.AppointmentMapper;
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
public class AppointmentMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private AppointmentMapper mapper;

    @Test
    public void testThatToEntityWorksWell() {
        // given
        AppointmentDto appointmentDto = AppointmentDto.builder().reason("Something").build();
        AppointmentEntity appointmentEntity = AppointmentEntity.builder().reason("Something").build();
        // when
        when(modelMapper.map(appointmentDto, AppointmentEntity.class)).thenReturn(appointmentEntity);
        AppointmentEntity result = mapper.toEntity(appointmentDto);
        // then
        assertEquals(result, appointmentEntity);
        verify(modelMapper, times(1)).map(appointmentDto, AppointmentEntity.class);
    }

    @Test
    public void testThatToDtoWorksWell() {
        // given
        AppointmentDto appointmentDto = AppointmentDto.builder().reason("Something").build();
        AppointmentEntity appointmentEntity = AppointmentEntity.builder().reason("Something").build();
        // when
        when(modelMapper.map(appointmentEntity, AppointmentDto.class)).thenReturn(appointmentDto);
        AppointmentDto result = mapper.toDto(appointmentEntity);
        // then
        assertEquals(result, appointmentDto);
        verify(modelMapper, times(1)).map(appointmentEntity, AppointmentDto.class);
    }


}
