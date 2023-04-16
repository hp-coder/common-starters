package com.luban.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hp 2023/4/16
 */
@ConfigurationProperties("spring.elasticsearch")
public class CustomElasticsearchProperties {

    private boolean enableRestClient = false;

    public boolean isEnableRestClient() {
        return enableRestClient;
    }

    public void setEnableRestClient(boolean enableRestClient) {
        this.enableRestClient = enableRestClient;
    }
}
