package com.hp.dingtalk.component.application;

/**
 * 钉钉miniH5企业内部应用
 *
 * @author hp
 */
public interface IDingMiniH5 extends IDingApp {

    /**
     * 事件回调配置, HTTP推送, 加密 aes_key
     */
    default String getEventKey() {
        return null;
    }

    /**
     * 事件回调配置, HTTP推送, 签名 token
     */
    default String getEventToken() {
        return null;
    }
}
