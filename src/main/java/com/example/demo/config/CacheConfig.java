package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String CACHE_TASKS_ALL = "tasksAllByUser";
    public static final String CACHE_TASKS_PENDING = "tasksPendingByUser";

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port
    ) {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory, CacheProperties cacheProperties) {
        RedisSerializer<String> keySerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

        long ttlMs = cacheProperties.getRedis() != null && cacheProperties.getRedis().getTimeToLive() != null
                ? cacheProperties.getRedis().getTimeToLive().toMillis()
                : 60000;

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .disableCachingNullValues()
                .entryTtl(Duration.ofMillis(ttlMs));

        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        perCache.put(CACHE_TASKS_ALL, defaultConfig.entryTtl(Duration.ofSeconds(60)));
        perCache.put(CACHE_TASKS_PENDING, defaultConfig.entryTtl(Duration.ofSeconds(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(perCache)
                .build();
    }
}
