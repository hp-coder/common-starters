package com.hp.dingtalk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hp
 */
@ConfigurationProperties("dingtalk.miniH5")
public @Data class DingTalkMiniH5Properties {

    private EventConfig event;

    @Data
    @ConfigurationProperties("dingtalk.miniH5.event")
    public static class EventConfig {
        private boolean enabled;
        private ListenerConfig listener;
    }

    @Data
    @ConfigurationProperties("dingtalk.miniH5.event.listener")
    public static class ListenerConfig {
        private boolean enabled;
    }
}
