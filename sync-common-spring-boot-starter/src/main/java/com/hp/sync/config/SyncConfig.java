package com.hp.sync.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author hp
 */
@Data
@ConfigurationProperties(prefix = "sync")
public class SyncConfig {

    private DingTalk dingTalk;

    @Data
    public static class DingTalk {
        private List<String> dingTalkNotifyMobile;
        private String appName;
        private Long appId;
        private String appKey;
        private String appSecret;
    }


}
