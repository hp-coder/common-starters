package com.hp.redis.registry.template;

import cn.hutool.extra.spring.SpringUtil;
import com.hp.redis.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @author hp
 */
@Slf4j
public class RedisTemplateRegistry {

    private final List<RedisConnectionFactory> factories;

    public RedisTemplateRegistry(List<RedisConnectionFactory> factories) {
        this.factories = factories;
        this.register();
    }

    private void register() throws BeansException {
        factories.forEach(factory -> RedisHelper.getDatabase(factory).ifPresent(database -> {
            SpringUtil.registerBean("redisTemplate" + database, createRedisTemplate(factory));
            log.debug("RedisTemplate-{} has registered.", database);

            SpringUtil.registerBean("stringRedisTemplate" + database, createStringRedisTemplate(factory));
            log.debug("StringRedisTemplate-{} has registered.", database);
        }));
    }

    private StringRedisTemplate createStringRedisTemplate(RedisConnectionFactory factory) {
        final StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(factory);
        stringRedisTemplate.afterPropertiesSet();
        return stringRedisTemplate;
    }

    private static RedisTemplate<Object, Object> createRedisTemplate(RedisConnectionFactory factory) {
        final RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.afterPropertiesSet();
        return template;
    }
}
