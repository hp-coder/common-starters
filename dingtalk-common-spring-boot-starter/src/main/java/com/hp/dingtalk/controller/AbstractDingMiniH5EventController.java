package com.hp.dingtalk.controller;

import com.google.common.base.Preconditions;
import com.hp.dingtalk.pojo.callback.DingMiniH5EventCallbackRequest;
import com.hp.dingtalk.utils.DingCallbackCrypto;
import com.hp.dingtalk.component.application.IDingMiniH5;
import com.hp.dingtalk.component.configuration.DingMiniH5EventCallbackConfig;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;

/**
 * @author hp
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractDingMiniH5EventController {

    protected final DingMiniH5EventCallbackConfig callbackConfiguration;
    protected final ApplicationEventPublisher eventPublisher;
    protected DingCallbackCrypto callbackCrypto;


    protected String decrypt(DingMiniH5EventCallbackRequest request, EventPayload payload) throws DingCallbackCrypto.DingTalkEncryptException {
        Preconditions.checkArgument(Objects.nonNull(callbackConfiguration), "请实现DingMiniH5EventCallbackConfig并提供一个有效的钉钉miniH5微应用实例");
        final IDingMiniH5 app = callbackConfiguration.getApp();
        Preconditions.checkArgument(Objects.nonNull(app), "钉钉miniH5微应用实例为空");
        // 钉钉提供的加解密工具
        callbackCrypto = new DingCallbackCrypto(app.getEventToken(), app.getEventKey(), app.getAppKey());
        String encryptMsg = payload.getEncrypt();
        return callbackCrypto.getDecryptMsg(request.getSignature(), request.getTimestamp(), request.getNonce(), encryptMsg);
    }

    @Data
    public static class EventPayload {
        private String encrypt;
    }
}
