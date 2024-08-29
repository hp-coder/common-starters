package com.hp.dingtalk.component.configuration;

import com.hp.dingtalk.component.application.IDingMiniH5;

/**
 * 客户端必须实现该接口, 使得事件回调可以获取到动态的应用信息;
 *
 * @author hp
 */
public interface DingMiniH5EventCallbackConfig {
    IDingMiniH5 getApp();
}
