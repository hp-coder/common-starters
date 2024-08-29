package com.hp.redis;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.core.RedisOperations;

import java.util.LinkedHashSet;
import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@ConditionalOnClass(RedisOperations.class)
public class MultiDatabaseRedisConfiguration {

    private final RedisProperties redisProperties;
    private final MultiDatabaseRedisProperties multiDatabaseRedisProperties;

    public MultiDatabaseRedisConfiguration(
            RedisProperties redisProperties,
            MultiDatabaseRedisProperties multiDatabaseRedisProperties
    ) {
        this.redisProperties = redisProperties;
        this.multiDatabaseRedisProperties = multiDatabaseRedisProperties;
        this.register();
    }

    private static RedisStandaloneConfiguration createConfiguration(RedisProperties properties) {
        final RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(properties.getHost(), properties.getPort());
        Optional.ofNullable(properties.getUsername()).ifPresent(configuration::setUsername);
        Optional.ofNullable(properties.getPassword()).ifPresent(configuration::setPassword);
        return configuration;
    }

    private void register() {
        if (!multiDatabaseRedisProperties.isMultiple()) {
            return;
        }
        final LinkedHashSet<Integer> databases = multiDatabaseRedisProperties.getDatabases();
        databases.forEach(database -> {
            final RedisStandaloneConfiguration configuration = createConfiguration(redisProperties);
            configuration.setDatabase(database);
            SpringUtil.registerBean("redisStandaloneConfiguration" + database, configuration);
            log.debug("RedisStandaloneConfiguration-{} has registered.", database);
        });
    }
}
