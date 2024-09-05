package com._olelllka.HealthSphere_Backend.mapper;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorListDto;
import com._olelllka.HealthSphere_Backend.mapper.impl.DoctorDocumentMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorDocumentMapperUnitTest {

    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private DoctorDocumentMapper mapper;

    @Test
    public void testThatDoctorDetailMapperMapsToDto() {
        // given
        DoctorDocument doctor = DoctorDocument.builder().firstName("First Name").build();
        DoctorListDto expected = DoctorListDto.builder().firstName("First Name").build();
        // when
        when(modelMapper.map(doctor, DoctorListDto.class)).thenReturn(expected);
        DoctorListDto result = mapper.toDto(doctor);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result, expected)
        );
        verify(modelMapper, times(1)).map(doctor, DoctorListDto.class);
    }


}
