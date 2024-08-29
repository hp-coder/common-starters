package com.hp.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;

/**
 * @author hp
 */
@Data
@ConfigurationProperties(prefix = "spring.data.redis")
public class MultiDatabaseRedisProperties {

    private boolean multiple = false;

    private LinkedHashSet<Integer> databases;
}
