package com.hp.redis.registry.factory.lettuce;

import cn.hutool.extra.spring.SpringUtil;
import com.hp.redis.RedisHelper;
import com.hp.redis.registry.factory.MultiDatabaseRedisConnectionRegistry;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.RedisClient;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.ssl.SslOptions;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

/**
 * @author hp
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RedisClient.class)
@ConditionalOnProperty(name = "spring.data.redis.client-type", havingValue = "lettuce", matchIfMissing = true)
public class LettuceConnectionFactoryRegistry extends MultiDatabaseRedisConnectionRegistry {
    private final ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers;
    private final ClientResources clientResources;

    public LettuceConnectionFactoryRegistry(
            Environment environment,
            RedisProperties redisProperties,
            List<RedisStandaloneConfiguration> redisStandaloneConfigurations,
            ObjectProvider<RedisSentinelConfiguration> redisSentinelConfigurations,
            ObjectProvider<RedisClusterConfiguration> redisClusterConfigurations,
            ObjectProvider<RedisConnectionDetails> redisConnectionDetails,
            ObjectProvider<SslBundles> sslBundles,
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers,
            ClientResources clientResources
    ) {
        super(
                environment,
                redisProperties,
                redisStandaloneConfigurations,
                redisSentinelConfigurations,
                redisClusterConfigurations,
                redisConnectionDetails,
                sslBundles
        );
        this.customizers = customizers;
        this.clientResources = clientResources;
        this.register();
    }

    @Override
    protected void register() {
        List<LettuceConnectionFactory> factories = createFactories();
        factories.forEach(factory -> RedisHelper.getDatabase(factory).ifPresent(database -> {
            factory.afterPropertiesSet();
            SpringUtil.registerBean("redisConnectionFactory" + database, factory);
            log.debug("Lettuce: redisConnectionFactory-{} has registered.",database);
        }));
        log.debug("The default redis database is {}", getPrimaryDatabase());
    }

    protected List<LettuceConnectionFactory> createFactories() {
        if (isVirtualThreadEnabled()) {
            return createVirtualConnectionFactories(customizers, clientResources);
        } else {
            return createPlatformConnectionFactories(customizers, clientResources);
        }
    }

    private List<LettuceConnectionFactory> createPlatformConnectionFactories(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers,
            ClientResources clientResources
    ) {
        return createConnectionFactory(customizers, clientResources);
    }

    private List<LettuceConnectionFactory> createVirtualConnectionFactories(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers,
            ClientResources clientResources
    ) {
        List<LettuceConnectionFactory> factories = createConnectionFactory(customizers, clientResources);
        return factories.stream()
                .peek(factory -> {
                    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor("db%s-redis-".formatted(factory.getDatabase()));
                    executor.setVirtualThreads(true);
                    factory.setExecutor(executor);
                })
                .toList();
    }

    private List<LettuceConnectionFactory> createConnectionFactory(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers,
            ClientResources clientResources
    ) {
        final LettuceClientConfiguration clientConfig = getLettuceClientConfiguration(customizers, clientResources, getProperties().getLettuce().getPool());
        return createLettuceConnectionFactory(clientConfig);
    }

    private List<LettuceConnectionFactory> createLettuceConnectionFactory(LettuceClientConfiguration clientConfiguration) {
        if (getSentinelConfig() != null) {
            return Collections.singletonList(new LettuceConnectionFactory(getSentinelConfig(), clientConfiguration));
        }
        if (getClusterConfiguration() != null) {
            return Collections.singletonList(new LettuceConnectionFactory(getClusterConfiguration(), clientConfiguration));
        }
        return getStandaloneConfigs().stream()
                .map(standalone -> new LettuceConnectionFactory(standalone, clientConfiguration))
                .toList();
    }

    private LettuceClientConfiguration getLettuceClientConfiguration(
            ObjectProvider<LettuceClientConfigurationBuilderCustomizer> customizers,
            ClientResources clientResources,
            RedisProperties.Pool pool
    ) {
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = createBuilder(pool);
        applyProperties(builder);
        if (StringUtils.hasText(getProperties().getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builder.clientOptions(createClientOptions());
        builder.clientResources(clientResources);
        customizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool pool) {
        if (isPoolEnabled(pool)) {
            return new LettuceConnectionFactoryRegistry.PoolBuilderFactory().createBuilder(pool);
        }
        return LettuceClientConfiguration.builder();
    }

    private void applyProperties(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (isSslEnabled()) {
            builder.useSsl();
        }
        if (getProperties().getTimeout() != null) {
            builder.commandTimeout(getProperties().getTimeout());
        }
        if (getProperties().getLettuce() != null) {
            RedisProperties.Lettuce lettuce = getProperties().getLettuce();
            if (lettuce.getShutdownTimeout() != null && !lettuce.getShutdownTimeout().isZero()) {
                builder.shutdownTimeout(getProperties().getLettuce().getShutdownTimeout());
            }
        }
        if (StringUtils.hasText(getProperties().getClientName())) {
            builder.clientName(getProperties().getClientName());
        }
    }

    private ClientOptions createClientOptions() {
        ClientOptions.Builder builder = initializeClientOptionsBuilder();
        Duration connectTimeout = getProperties().getConnectTimeout();
        if (connectTimeout != null) {
            builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
        }
        if (isSslEnabled() && getProperties().getSsl().getBundle() != null) {
            SslBundle sslBundle = getSslBundles().getBundle(getProperties().getSsl().getBundle());
            io.lettuce.core.SslOptions.Builder sslOptionsBuilder = io.lettuce.core.SslOptions.builder();
            sslOptionsBuilder.keyManager(sslBundle.getManagers().getKeyManagerFactory());
            sslOptionsBuilder.trustManager(sslBundle.getManagers().getTrustManagerFactory());
            SslOptions sslOptions = sslBundle.getOptions();
            if (sslOptions.getCiphers() != null) {
                sslOptionsBuilder.cipherSuites(sslOptions.getCiphers());
            }
            if (sslOptions.getEnabledProtocols() != null) {
                sslOptionsBuilder.protocols(sslOptions.getEnabledProtocols());
            }
            builder.sslOptions(sslOptionsBuilder.build());
        }
        return builder.timeoutOptions(TimeoutOptions.enabled()).build();
    }

    private ClientOptions.Builder initializeClientOptionsBuilder() {
        if (getProperties().getCluster() != null) {
            ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
            RedisProperties.Lettuce.Cluster.Refresh refreshProperties = getProperties().getLettuce().getCluster().getRefresh();
            ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder()
                    .dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
            if (refreshProperties.getPeriod() != null) {
                refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
            }
            if (refreshProperties.isAdaptive()) {
                refreshBuilder.enableAllAdaptiveRefreshTriggers();
            }
            return builder.topologyRefreshOptions(refreshBuilder.build());
        }
        return ClientOptions.builder();
    }

    private void customizeConfigurationFromUrl(LettuceClientConfiguration.LettuceClientConfigurationBuilder builder) {
        if (urlUsesSsl()) {
            builder.useSsl();
        }
    }

    /**
     * Inner class to allow optional commons-pool2 dependency.
     */
    private static class PoolBuilderFactory {

        LettuceClientConfiguration.LettuceClientConfigurationBuilder createBuilder(RedisProperties.Pool properties) {
            return LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(properties));
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWait(properties.getMaxWait());
            }
            return config;
        }

    }
}
