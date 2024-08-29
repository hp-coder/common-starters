package com.hp.redis.registry.factory.jedis;

import cn.hutool.extra.spring.SpringUtil;
import com.hp.redis.RedisHelper;
import com.hp.redis.registry.factory.MultiDatabaseRedisConnectionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import javax.net.ssl.SSLParameters;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author hp
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ GenericObjectPool.class, JedisConnection.class, Jedis.class })
@ConditionalOnMissingBean(RedisConnectionFactory.class)
@ConditionalOnProperty(name = "spring.data.redis.client-type", havingValue = "jedis", matchIfMissing = true)
public class JedisConnectionFactoryRegistry extends MultiDatabaseRedisConnectionRegistry {

    private final ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers;

    public JedisConnectionFactoryRegistry(
            Environment environment,
            RedisProperties redisProperties,
            List<RedisStandaloneConfiguration> redisStandaloneConfigurations,
            ObjectProvider<RedisSentinelConfiguration> redisSentinelConfigurations,
            ObjectProvider<RedisClusterConfiguration> redisClusterConfigurations,
            ObjectProvider<RedisConnectionDetails> redisConnectionDetails,
            ObjectProvider<SslBundles> sslBundles,
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers
    ) {
        super(environment, redisProperties, redisStandaloneConfigurations, redisSentinelConfigurations, redisClusterConfigurations, redisConnectionDetails, sslBundles);
        this.customizers = customizers;
        this.register();
    }

    @Override
    protected void register() {
        List<JedisConnectionFactory> factories = createFactories();
        factories.forEach(factory -> RedisHelper.getDatabase(factory).ifPresent(database -> {
            factory.afterPropertiesSet();
            SpringUtil.registerBean("redisConnectionFactory" + database, factory);
            log.debug("Jedis: redisConnectionFactory-{} has registered.",database);
        }));
        log.debug("The default redis database is {}", getPrimaryDatabase());
    }

    protected List<JedisConnectionFactory> createFactories() {
        if (isVirtualThreadEnabled()) {
            return createVirtualConnectionFactories(customizers);
        } else {
            return createPlatformConnectionFactories(customizers);
        }
    }

    private List<JedisConnectionFactory> createVirtualConnectionFactories(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers
    ) {
        final List<JedisConnectionFactory> factories = createJedisConnectionFactory(customizers);
        return factories.stream()
                .peek(factory -> {
                    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("db%s-redis-".formatted(factory.getDatabase()));
                    executor.setVirtualThreads(true);
                    factory.setExecutor(executor);
                })
                .toList();
    }

    private List<JedisConnectionFactory> createPlatformConnectionFactories(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers
    ) {
        return createJedisConnectionFactory(customizers);
    }

    private List<JedisConnectionFactory> createJedisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers) {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(customizers);
        if (getSentinelConfig() != null) {
            return Collections.singletonList(new JedisConnectionFactory(getSentinelConfig(), clientConfiguration));
        }
        if (getClusterConfiguration() != null) {
            return Collections.singletonList(new JedisConnectionFactory(getClusterConfiguration(), clientConfiguration));
        }
        return getStandaloneConfigs().stream()
                .map(standalone -> new JedisConnectionFactory(standalone, clientConfiguration))
                .toList();
    }

    private JedisClientConfiguration getJedisClientConfiguration(ObjectProvider<JedisClientConfigurationBuilderCustomizer> customizers) {
        JedisClientConfiguration.JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        if (isSslEnabled()) {
            applySsl(builder);
        }
        RedisProperties.Pool pool = getProperties().getJedis().getPool();
        if (isPoolEnabled(pool)) {
            applyPooling(pool, builder);
        }
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        Optional.ofNullable(customizers).ifPresent(c -> c.orderedStream().forEach((customizer) -> customizer.customize(builder)));
        return builder.build();
    }

    private JedisClientConfiguration.JedisClientConfigurationBuilder applyProperties(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(getProperties().getTimeout()).to(builder::readTimeout);
        map.from(getProperties().getConnectTimeout()).to(builder::connectTimeout);
        map.from(getProperties().getClientName()).whenHasText().to(builder::clientName);
        return builder;
    }

    private void applySsl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        JedisClientConfiguration.JedisSslClientConfigurationBuilder sslBuilder = builder.useSsl();
        if (getProperties().getSsl().getBundle() != null) {
            SslBundle sslBundle = getSslBundles().getBundle(getProperties().getSsl().getBundle());
            sslBuilder.sslSocketFactory(sslBundle.createSslContext().getSocketFactory());
            SslOptions sslOptions = sslBundle.getOptions();
            SSLParameters sslParameters = new SSLParameters();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            map.from(sslOptions.getCiphers()).to(sslParameters::setCipherSuites);
            map.from(sslOptions.getEnabledProtocols()).to(sslParameters::setProtocols);
            sslBuilder.sslParameters(sslParameters);
        }
    }

    private void applyPooling(RedisProperties.Pool pool,
                              JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        builder.usePooling().poolConfig(jedisPoolConfig(pool));
    }

    private JedisPoolConfig jedisPoolConfig(RedisProperties.Pool pool) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(pool.getMaxActive());
        config.setMaxIdle(pool.getMaxIdle());
        config.setMinIdle(pool.getMinIdle());
        if (pool.getTimeBetweenEvictionRuns() != null) {
            config.setTimeBetweenEvictionRuns(pool.getTimeBetweenEvictionRuns());
        }
        if (pool.getMaxWait() != null) {
            config.setMaxWait(pool.getMaxWait());
        }
        return config;
    }

    private void customizeConfigurationFromUrl(JedisClientConfiguration.JedisClientConfigurationBuilder builder) {
        if (urlUsesSsl()) {
            builder.useSsl();
        }
    }

}
