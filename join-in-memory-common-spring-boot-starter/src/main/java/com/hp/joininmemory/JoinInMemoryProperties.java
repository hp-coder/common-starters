package com.hp.joininmemory;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @version 1.0.0
 * @developers <a href="mailto:max_verstrappon@outlook.com">Hu Peng</a>
 * @date 2026/1/7
 */
@Data
@ConfigurationProperties(prefix = "join-in-memory")
public class JoinInMemoryProperties {

    /**
     * Metrics Configurations
     */
    private Metrics metrics = new Metrics();

    @Data
    public static class Metrics {

        /**
         * allow micrometers to collect metrics information on class join operations.
         */
        private boolean enableMetrics = true;

        /**
         * allow micrometers to collect detailed metrics information on field join operations.
         */
        private boolean enableDetailedMetrics = false;
    }
}
