package com.hp.redis;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@UtilityClass
public class RedisHelper {

    public Optional<StringRedisTemplate> getStringRedisTemplateByDb(int dbIndex) {
        final Map<String, StringRedisTemplate> beans = SpringUtil.getBeansOfType(StringRedisTemplate.class);
        if (MapUtil.isEmpty(beans)) {
            return Optional.empty();
        }
        return beans.values().stream().filter(template -> {
            final Optional<Integer> database = getDatabase(template.getConnectionFactory());
            return database.filter(integer -> Objects.equals(integer, dbIndex)).isPresent();
        }).findFirst();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Optional<RedisTemplate<Object, Object>> getRedisTemplateByDb(int dbIndex) {
        final Map<String, RedisTemplate> beans = SpringUtil.getBeansOfType(RedisTemplate.class);
        if (MapUtil.isEmpty(beans)) {
            return Optional.empty();
        }
        return beans.values().stream().filter(template -> {
            final Optional<Integer> database = getDatabase(template.getConnectionFactory());
            return database.filter(integer -> Objects.equals(integer, dbIndex)).isPresent();
        }).findFirst().map(i -> (RedisTemplate<Object, Object>) i);
    }


    public static Optional<Integer> getDatabase(RedisConnectionFactory factory) {
        int database;
        if (factory instanceof LettuceConnectionFactory lettuceConnectionFactory) {
            database = lettuceConnectionFactory.getDatabase();
        } else if (factory instanceof JedisConnectionFactory jedisConnectionFactory) {
            database = jedisConnectionFactory.getDatabase();
        } else {
            // RedissonConnectionFactory, etc.
            return Optional.empty();
        }
        return Optional.of(database);
    }
}
