package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.AbstractTestContainers;
import com._olelllka.HealthSphere_Backend.domain.documents.MedicalRecordDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MedicalRecordElasticRepositoryIntegrationTest extends AbstractTestContainers {

    @Autowired
    private MedicalRecordElasticRepository elasticRepository;


    @BeforeEach
    void initEach() {
        elasticRepository.deleteAll();
        MedicalRecordDocument document = MedicalRecordDocument.builder()
                .id(1L)
                .user_id(1L)
                .diagnosis("Some diagnosis")
                .recordDate(LocalDate.of(2020, Month.APRIL, 10))
                .treatment("Some treatment")
                .doctor("DOCTOR DOCTOR").build();
        elasticRepository.save(document);
    }

    @Test
    public void testThatFindByParamsReturnsPageOfResultsWhenAllOfParamsArePresent() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<MedicalRecordDocument> result = elasticRepository
                .findByParams(1L,
                        "Some diagnosis",
                        LocalDate.of(2019, Month.APRIL, 1).toString(),
                        LocalDate.of(2021, Month.AUGUST, 10).toString(),
                        pageable);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getId(), 1L)
        );
    }

    @Test
    public void testThatFindByParamsReturnsPageOfResultsWhenNotAllParamsArePresent() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<MedicalRecordDocument> result = elasticRepository
                .findByParams(1L,
                        "",
                        LocalDate.of(2019, Month.APRIL, 1).toString(),
                        LocalDate.of(2021, Month.AUGUST, 10).toString(),
                        pageable);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 1),
                () -> assertEquals(result.getContent().get(0).getId(), 1L)
        );
    }

    @Test
    public void testThatFindByParamsReturnsPageOfWithoutResultsWhenSentWrongParams() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<MedicalRecordDocument> result = elasticRepository
                .findByParams(1L,
                        "another string",
                        LocalDate.of(2021, Month.APRIL, 1).toString(),
                        LocalDate.of(2021, Month.AUGUST, 10).toString(),
                        pageable);
        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 0)
        );
    }

}
