package com._olelllka.HealthSphere_Backend.repositories;

import com._olelllka.HealthSphere_Backend.TestContainers;
import com._olelllka.HealthSphere_Backend.domain.documents.PatientDocument;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class PatientElasticRepositoryIntegrationTest {

    @Container
    static ElasticsearchContainer container = TestContainers.elasticsearchContainer;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", container::getHttpHostAddress);
    }

    @Autowired
    private PatientElasticRepository elasticRepository;

    @BeforeAll
    static void setUp() {
        container.start();
    }

    @AfterAll
    static void tearDown() {
        container.stop();
    }

    @BeforeEach
    void initEach() {
        elasticRepository.deleteAll();
        PatientDocument patientDocument = PatientDocument
                .builder()
                .firstName("First Name")
                .lastName("Last Name")
                .email("email@email.com")
                .id(1L).build();
        elasticRepository.save(patientDocument);
    }

    @Test
    public void testThatFindByParamsReturnsPageOfResults() {
        assertTrue(elasticRepository.existsById(1L));
        Pageable pageable = PageRequest.of(0, 1);
        Page<PatientDocument> result = elasticRepository.findByParams("First Last", pageable);

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
        Page<PatientDocument> result = elasticRepository.findByParams("email@email.com", pageable);
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
        Page<PatientDocument> result = elasticRepository.findByParams("Incorrect String", pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(result.getContent().size(), 0)
        );
    }
}
