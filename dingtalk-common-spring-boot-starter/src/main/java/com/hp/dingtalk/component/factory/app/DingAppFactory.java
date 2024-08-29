package com.hp.dingtalk.component.factory.app;

import com.google.common.base.Preconditions;
import com.hp.dingtalk.component.application.IDingApp;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 钉钉应用工厂
 * <p>
 * 既然已经使用了spring框架，直接使用其容器完成单例效果
 * <p>
 * 实现IDingApp接口并注册到spring容器中的类将被自动添加到工厂缓存中
 * it's a dummy factory. it pretends to be a real factory
 *
 * @author hp
 */
@Slf4j
@UtilityClass
public class DingAppFactory {
    private static final Map<String, IDingApp> KEY_CACHE = new HashMap<>(8);
    private static final Map<Long, IDingApp> ID_CACHE = new HashMap<>(8);

    public static void setAppCache(IDingApp app) {
        Preconditions.checkArgument(Objects.nonNull(app), "应用不能为空");
        KEY_CACHE.put(app.getAppKey(), app);
        ID_CACHE.put(app.getAppId(), app);
    }

    public static <T extends IDingApp> T app(Class<T> clazz) {
        return CollectionUtils.findValueOfType(KEY_CACHE.values(), clazz);
    }

    public static <T extends IDingApp> T app(Long appId) {
        return (T) ID_CACHE.get(appId);
    }

    public static <T extends IDingApp> T app(String key) {
        return (T) KEY_CACHE.get(key);
    }
}
