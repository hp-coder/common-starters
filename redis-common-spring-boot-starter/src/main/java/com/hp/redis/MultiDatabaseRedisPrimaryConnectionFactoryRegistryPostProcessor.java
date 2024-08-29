package com.hp.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;

/**
 * @author hp
 */
@Slf4j
@ConditionalOnProperty(value = "spring.data.redis.multiple", havingValue = "true")
@AutoConfiguration(after = RedisReactiveAutoConfiguration.class, before = MultiDatabaseRedisAutoConfiguration.class)
public class MultiDatabaseRedisPrimaryConnectionFactoryRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        log.debug("Set the original redis connection factory as primary.");
        if (registry.containsBeanDefinition("redissonConnectionFactory")) {
            registry.getBeanDefinition("redissonConnectionFactory").setPrimary(true);
        } else if (registry.containsBeanDefinition("redisConnectionFactory")) {
            registry.getBeanDefinition("redisConnectionFactory").setPrimary(true);
        } else if (registry.containsBeanDefinition("redisConnectionFactoryVirtualThreads")) {
            registry.getBeanDefinition("redisConnectionFactoryVirtualThreads").setPrimary(true);
        }
    }
}
