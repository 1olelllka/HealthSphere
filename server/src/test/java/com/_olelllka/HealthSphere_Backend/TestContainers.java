package com._olelllka.HealthSphere_Backend;

import com.redis.testcontainers.RedisContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;

public class TestContainers {

    public static ElasticsearchContainer elasticsearchContainer =
            new ElasticsearchContainer(DockerImageName.parse("elasticsearch").withTag("7.17.23"));

    public static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer(
            DockerImageName.parse("rabbitmq").withTag("3.13-management")
    );
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis"))
            .withAccessToHost(true)
            .withExposedPorts(6379);

}
