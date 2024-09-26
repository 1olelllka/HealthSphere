package com._olelllka.HealthSphere_Backend;

import com.redis.testcontainers.RedisStackContainer;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public abstract class AbstractTestContainers {

    public static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch").withTag("7.17.23"));

    public static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer(
                    DockerImageName.parse("rabbitmq").withTag("3.13-management"));

    public static RedisStackContainer redisStackContainer =
            new RedisStackContainer(DockerImageName.parse("redis/redis-stack"))
                    .withExposedPorts(6379)
                    .waitingFor(org.testcontainers.containers.wait.strategy.Wait.forListeningPort());

    @DynamicPropertySource
    public static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
        registry.add("spring.redis.host", redisStackContainer::getHost);
        registry.add("spring.redis.port", () -> redisStackContainer.getFirstMappedPort());
    }

    static {
        rabbitMQContainer.start();
        elasticsearchContainer.start();
        redisStackContainer.start();
    }
}
