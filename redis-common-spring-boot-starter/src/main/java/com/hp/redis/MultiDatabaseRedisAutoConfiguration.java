package com.hp.redis;

import com.hp.redis.registry.factory.jedis.JedisConnectionFactoryRegistry;
import com.hp.redis.registry.factory.lettuce.LettuceConnectionFactoryRegistry;
import com.hp.redis.registry.template.RedisTemplateRegistry;
import io.lettuce.core.RedisClient;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author hp
 */

@Slf4j
@ConditionalOnClass(RedisOperations.class)
@Import({MultiDatabaseRedisProperties.class})
@ConditionalOnProperty(value = "spring.data.redis.multiple", havingValue = "true")
public class MultiDatabaseRedisAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MultiDatabaseRedisConfiguration multiDatabaseRedisConfiguration(
            RedisProperties redisProperties,
            MultiDatabaseRedisProperties multiDatabaseRedisProperties
    ) {
        return new MultiDatabaseRedisConfiguration(
                redisProperties,
                multiDatabaseRedisProperties
        );
    }

    @Bean
    @ConditionalOnClass(RedisClient.class)
    @ConditionalOnProperty(name = "spring.data.redis.client-type", havingValue = "lettuce", matchIfMissing = true)
    public LettuceConnectionFactoryRegistry lettuceConnectionFactoryRegistry(
            Environment environment,
            RedisProperties redisProperties,
            List<RedisStandaloneConfiguration> redisStandaloneConfigurations,
            ObjectProvider<RedisSentinelConfiguration> redisSentinelConfigurations,
            ObjectProvider<RedisClusterConfiguration> redisClusterConfigurations,
            ObjectProvider<RedisConnectionDetails> redisConnectionDetails,
            ObjectProvider<SslBundles> sslBundles,
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers,
            ClientResources defaultClientResources

    ) {
        return new LettuceConnectionFactoryRegistry(
                environment,
                redisProperties,
                redisStandaloneConfigurations,
                redisSentinelConfigurations,
                redisClusterConfigurations,
                redisConnectionDetails,
                sslBundles,
                customizers,
                defaultClientResources
        );
    }

    @Bean
    @ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
    @ConditionalOnProperty(name = "spring.data.redis.client-type", havingValue = "jedis", matchIfMissing = true)
    public JedisConnectionFactoryRegistry jedisConnectionFactoryRegistry(
            Environment environment,
            RedisProperties redisProperties,
            List<RedisStandaloneConfiguration> redisStandaloneConfigurations,
            ObjectProvider<RedisSentinelConfiguration> redisSentinelConfigurations,
            ObjectProvider<RedisClusterConfiguration> redisClusterConfigurations,
            ObjectProvider<RedisConnectionDetails> redisConnectionDetails,
            ObjectProvider<SslBundles> sslBundles,
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers
    ) {
        return new JedisConnectionFactoryRegistry(
                environment,
                redisProperties,
                redisStandaloneConfigurations,
                redisSentinelConfigurations,
                redisClusterConfigurations,
                redisConnectionDetails,
                sslBundles,
                customizers
        );
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplateRegistry redisTemplateRegistry(List<RedisConnectionFactory> factories) {
        return new RedisTemplateRegistry(factories);
    }
}
