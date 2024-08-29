package com.hp.dingtalk.component.application;

import com.hp.dingtalk.component.factory.app.DingAppFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * 钉钉应用
 * 整个项目基于Spring容器能力，所以最好将APP实现注册到IoC容器中
 *
 * @author hp
 */
public interface IDingApp extends IDingCorp, SmartInitializingSingleton {

    String getAppName();

    String getAppKey();

    String getAppSecret();

    Long getAppId();

    @Override
    default void afterSingletonsInstantiated() {
        DingAppFactory.setAppCache(this);
    }
}
