package com._olelllka.HealthSphere_Backend.service.rabbitmq;

import com._olelllka.HealthSphere_Backend.domain.documents.DoctorDocument;
import com._olelllka.HealthSphere_Backend.domain.dto.doctors.DoctorDocumentDto;
import com._olelllka.HealthSphere_Backend.repositories.DoctorElasticRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class RabbitMessageConsumerIntegrationTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch").withTag("7.17.23"))
            ;

    @Container
    static RabbitMQContainer container = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq").withTag("3.13-management")
    );

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", container::getHost);
        registry.add("spring.rabbitmq.port", container::getAmqpPort);
        registry.add("spring.rabbitmq.username", container::getAdminUsername);
        registry.add("spring.rabbitmq.password", container::getAdminPassword);
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Autowired
    private DoctorElasticRepository elasticRepository;

    @Autowired
    private DoctorMessageProducer messageProducer;

    @Autowired
    private RabbitListenerEndpointRegistry listenerRegistry;

    @Autowired
    private RabbitAdmin admin;

    @BeforeAll
    static void setUp() {
        container.start();
        elasticsearchContainer.start();
    }

    @AfterAll
    static void tearDown() {
        container.stop();
        elasticsearchContainer.stop();
    }

    @Test
    public void testThatConsumeDoctorAddPerformsAddToElasticSearchDb() {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctors_index_queue")).getMessageCount());
        DoctorDocumentDto doctorDocument = DoctorDocumentDto
                .builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic")
                .experienceYears(5L)
                .build();
        listenerRegistry.getListenerContainer("doctor.post").start();
        messageProducer.sendDoctorToIndex(doctorDocument);
        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("doctors_index_queue").getMessageCount() == 0, Boolean.TRUE::equals);
        Optional<DoctorDocument> result = elasticRepository.findById(1L);
        assertTrue(result.isPresent());

        // tearDown
        elasticRepository.deleteById(1L);
    }

    @Test
    public void testThatConsumeDoctorDeletePerformsDeleteFromElasticSearchDb() {
        listenerRegistry.stop();
        assertEquals(0, Objects.requireNonNull(admin.getQueueInfo("doctor_index_delete_queue")).getMessageCount());
        DoctorDocument doctorDocument = DoctorDocument
                .builder()
                .id(1L)
                .firstName("First Name")
                .lastName("Last Name")
                .clinicAddress("Clinic")
                .experienceYears(5L)
                .build();
        elasticRepository.save(doctorDocument);
        listenerRegistry.getListenerContainer("doctor.delete").start();
        messageProducer.deleteDoctorFromIndex(1L);

        Awaitility.await().atMost(10, TimeUnit.SECONDS)
                .until(() -> admin.getQueueInfo("doctor_index_delete_queue").getMessageCount() == 0, Boolean.TRUE::equals);

        Optional<DoctorDocument> result = elasticRepository.findById(1L);
        assertTrue(result.isEmpty());
    }
}
