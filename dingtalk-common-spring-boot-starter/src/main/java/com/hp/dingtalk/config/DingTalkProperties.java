package com.hp.dingtalk.config;

import com.google.gson.GsonBuilder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hp
 */
@Slf4j
@Data
@ConfigurationProperties(prefix = "dingtalk")
public class DingTalkProperties implements SmartInitializingSingleton {

    private DingTalkMiniH5Properties miniH5;

    @Override
    public void afterSingletonsInstantiated() {
        log.debug(new GsonBuilder().create().toJson(this));
    }
}
