package com._olelllka.HealthSphere_Backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;


    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setDefaultSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(cf);
        return redisTemplate;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisConf = new RedisStandaloneConfiguration(redisHost, redisPort);
        return new JedisConnectionFactory(redisConf);
    }

    @Bean
    public CacheManager cacheManager(JedisConnectionFactory jedisConnectionFactory) {
        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(jedisConnectionFactory)
                .withCacheConfiguration("profile",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(60)))
                .withCacheConfiguration("specializations",
                        RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(1)))
                .build();
    }

    @Bean
    public KeyGenerator sha256KeyGenerator() {
        return new SHA256KeyGenerator();
    }

}
