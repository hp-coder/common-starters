/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hp.redis.registry.factory;

import cn.hutool.core.collection.CollUtil;
import com.hp.redis.RedisUrlSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Cluster;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Node;
import org.springframework.boot.autoconfigure.data.redis.RedisConnectionDetails.Sentinel;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Pool;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.ClassUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @see org.springframework.boot.autoconfigure.data.redis.RedisConnectionConfiguration
 */
@Slf4j
@ConditionalOnClass(RedisOperations.class)
public abstract class MultiDatabaseRedisConnectionRegistry {

    private static final boolean COMMONS_POOL2_AVAILABLE = ClassUtils.isPresent("org.apache.commons.pool2.ObjectPool",
            MultiDatabaseRedisConnectionRegistry.class.getClassLoader());

    protected final Environment environment;
    private final RedisProperties properties;

    private final List<RedisStandaloneConfiguration> standaloneConfigurationList;

    private final RedisSentinelConfiguration sentinelConfiguration;

    private final RedisClusterConfiguration clusterConfiguration;

    private final List<RedisConnectionDetails> connectionDetailsList;

    private final SslBundles sslBundles;

    protected MultiDatabaseRedisConnectionRegistry(
            Environment environment,
            RedisProperties redisProperties,
            List<RedisStandaloneConfiguration> redisStandaloneConfigurations,
            ObjectProvider<RedisSentinelConfiguration> redisSentinelConfigurations,
            ObjectProvider<RedisClusterConfiguration> redisClusterConfigurations,
            ObjectProvider<RedisConnectionDetails> redisConnectionDetails,
            ObjectProvider<SslBundles> sslBundles
    ) {
        this.environment = environment;
        this.properties = redisProperties;
        this.standaloneConfigurationList = redisStandaloneConfigurations;
        this.sentinelConfiguration = redisSentinelConfigurations.getIfAvailable();
        this.clusterConfiguration = redisClusterConfigurations.getIfAvailable();
        this.connectionDetailsList = redisConnectionDetails.orderedStream().toList();
        this.sslBundles = sslBundles.getIfAvailable();
    }

    protected abstract void register();

    protected final List<RedisStandaloneConfiguration> getStandaloneConfigs() {
        if (CollUtil.isNotEmpty(standaloneConfigurationList)) {
            return this.standaloneConfigurationList;
        } else {
            return Collections.emptyList();
        }
//        if (CollUtil.isEmpty(connectionDetailsList)) {
//            throw new IllegalArgumentException("At least one valid redis configuration has to be registered.");
//        }
//        return connectionDetailsList.stream()
//                .map(connectionDetails -> {
//                    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//                    config.setHostName(connectionDetails.getStandalone().getHost());
//                    config.setPort(connectionDetails.getStandalone().getPort());
//                    config.setUsername(connectionDetails.getUsername());
//                    config.setPassword(RedisPassword.of(connectionDetails.getPassword()));
//                    config.setDatabase(connectionDetails.getStandalone().getDatabase());
//                    return config;
//                })
//                .toList();
    }

    protected final RedisSentinelConfiguration getSentinelConfig() {
        if (this.sentinelConfiguration != null) {
            return this.sentinelConfiguration;
        }
        if (CollUtil.isEmpty(connectionDetailsList)) {
            return null;
        }
        final RedisConnectionDetails first = this.connectionDetailsList.getFirst();
        if (first.getSentinel() != null) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(first.getSentinel().getMaster());
            config.setSentinels(createSentinels(first.getSentinel()));
            config.setUsername(first.getUsername());
            String password = first.getPassword();
            if (password != null) {
                config.setPassword(RedisPassword.of(password));
            }
            config.setSentinelUsername(first.getSentinel().getUsername());
            String sentinelPassword = first.getSentinel().getPassword();
            if (sentinelPassword != null) {
                config.setSentinelPassword(RedisPassword.of(sentinelPassword));
            }
            config.setDatabase(first.getSentinel().getDatabase());
            return config;
        }
        return null;
    }

    protected final RedisClusterConfiguration getClusterConfiguration() {
        if (this.clusterConfiguration != null) {
            return this.clusterConfiguration;
        }
        if (CollUtil.isEmpty(connectionDetailsList)) {
            return null;
        }
        final RedisProperties.Cluster clusterProperties = this.properties.getCluster();
        final RedisConnectionDetails first = this.connectionDetailsList.getFirst();
        if (first.getCluster() != null) {
            RedisClusterConfiguration config = new RedisClusterConfiguration(
                    getNodes(first.getCluster()));
            if (clusterProperties != null && clusterProperties.getMaxRedirects() != null) {
                config.setMaxRedirects(clusterProperties.getMaxRedirects());
            }
            config.setUsername(first.getUsername());
            String password = first.getPassword();
            if (password != null) {
                config.setPassword(RedisPassword.of(password));
            }
            return config;
        }
        return null;
    }

    private List<String> getNodes(Cluster cluster) {
        return cluster.getNodes().stream().map((node) -> "%s:%d".formatted(node.host(), node.port())).toList();
    }

    protected final RedisProperties getProperties() {
        return this.properties;
    }

    protected SslBundles getSslBundles() {
        return this.sslBundles;
    }

    protected boolean isSslEnabled() {
        return getProperties().getSsl().isEnabled();
    }

    protected boolean isPoolEnabled(Pool pool) {
        Boolean enabled = pool.getEnabled();
        return (enabled != null) ? enabled : COMMONS_POOL2_AVAILABLE;
    }

    protected int getPrimaryDatabase() {
        return Math.max(0, properties.getDatabase());
    }

    protected boolean isVirtualThreadEnabled() {
        final Boolean enabled = environment.getProperty("spring.threads.virtual.enabled", Boolean.class);
        return Objects.equals(enabled, Boolean.TRUE);
    }

    private List<RedisNode> createSentinels(Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (Node node : sentinel.getNodes()) {
            nodes.add(new RedisNode(node.host(), node.port()));
        }
        return nodes;
    }

    protected final boolean urlUsesSsl() {
        return parseUrl(this.properties.getUrl()).useSsl();
    }

    protected final List<RedisConnectionDetails> getConnectionDetails() {
        return this.connectionDetailsList;
    }

    static ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            if (!"redis".equals(scheme) && !"rediss".equals(scheme)) {
                throw new RedisUrlSyntaxException(url);
            }
            boolean useSsl = ("rediss".equals(scheme));
            String username = null;
            String password = null;
            if (uri.getUserInfo() != null) {
                String candidate = uri.getUserInfo();
                int index = candidate.indexOf(':');
                if (index >= 0) {
                    username = candidate.substring(0, index);
                    password = candidate.substring(index + 1);
                } else {
                    password = candidate;
                }
            }
            return new ConnectionInfo(uri, useSsl, username, password);
        } catch (URISyntaxException ex) {
            throw new RedisUrlSyntaxException(url, ex);
        }
    }

    record ConnectionInfo(URI uri, boolean useSsl, String username, String password) {

    }

}
