package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.SpecializationDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class DoctorElasticRepositoryIntegrationTest extends AbstractTestContainers {

    @Autowired
    private DoctorElasticRepository elasticRepository;

    @BeforeEach
    void initEach() {
        elasticRepository.deleteAll();
        SpecializationDto specializations = SpecializationDto.builder().id(1L).specializationName("Specialization").build();
        DoctorDocument document = DoctorDocument.builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic Address")
                .experienceYears(5L)
                .specializations(List.of(specializations.getSpecializationName()))
                .build();
        elasticRepository.save(document);
    }

    @Test
    public void testThatFindByParamsReturnsPageOfResults() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<DoctorDocument> result = elasticRepository.findByParams("First Last", pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getFirstName(), "First Name")
        );
    }

    @Test
    public void testThatFindBySpecializationReturnsPageOfResults() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<DoctorDocument> result = elasticRepository.findByParams("Specialization", pageable);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getFirstName(), "First Name")
        );
    }

    @Test
    public void testThatFindByParamsReturnsPageOfWithoutResults() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<DoctorDocument> result = elasticRepository.findByParams("Incorrect String", pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 0)
        );
    }
}
