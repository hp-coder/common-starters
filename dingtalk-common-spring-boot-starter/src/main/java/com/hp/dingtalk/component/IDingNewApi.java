package com.hp.dingtalk.component;

import com.aliyun.teaopenapi.models.Config;
import com.hp.common.base.annotation.MethodDesc;

/**
 * @author hp
 */
public interface IDingNewApi extends IDingApi {
    SDK.Version NEW = SDK.Version.NEW;

    @MethodDesc("新版SDK的client配置")
    default Config getConfig() {
        Config config = new Config();
        config.protocol = "https";
        config.regionId = "central";
        return config;
    }
}
