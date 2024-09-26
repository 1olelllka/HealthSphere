package com._olelllka.HealthSphere_Backend.service;

import com._olelllka.HealthSphere_Backend.domain.entity.SpecializationEntity;
import com._olelllka.HealthSphere_Backend.repositories.SpecializationRepository;
import com._olelllka.HealthSphere_Backend.service.impl.SpecializationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SpecializationServiceUnitTest {

    @Mock
    private SpecializationRepository repository;
    @InjectMocks
    private SpecializationServiceImpl service;

    @Test
    public void testThatGetAllSpecializationsReturnsPageOfResults() {
        // given
        // when
        when(repository.findAll()).thenReturn(List.of());
        List<SpecializationEntity> result = service.getAllSpecializations();
        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.size(), 0)
        );
    }

}
