package com.hp.dingtalk.controller;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.gson.GsonBuilder;
import com.hp.dingtalk.constant.minih5event.DingMiniH5Event;
import com.hp.dingtalk.pojo.callback.DingMiniH5EventCallbackRequest;
import com.hp.dingtalk.pojo.callback.event.DingMiniH5CallbackEvents;
import com.hp.dingtalk.utils.DingCallbackCrypto;
import com.hp.dingtalk.component.configuration.DingMiniH5EventCallbackConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 钉钉微应用事件订阅机制, 鉴于只能绑定一个地址, 在模块中默认实现
 * 并利用Spring的事件广播机制对系统内发送钉钉事件, 以降低耦合度
 * <p>
 * The function needs to return the 'success' msg in 1500ms
 *
 * @author hp
 */
@Slf4j
@RestController
@RequestMapping("/ding/miniH5/event")
@ConditionalOnProperty(prefix = "dingtalk.miniH5.event", name = "enabled", havingValue = "true")
public class DingMiniH5EventController extends AbstractDingMiniH5EventController {

    public DingMiniH5EventController(
            DingMiniH5EventCallbackConfig callbackConfiguration,
            ApplicationEventPublisher eventPublisher
    ) {
        super(callbackConfiguration, eventPublisher);
    }

    @PostMapping("/callback")
    public Map<String, String> callback(
            // query parameters
            DingMiniH5EventCallbackRequest request,
            // application/json
            @RequestBody EventPayload payload
    ) {
        log.debug("钉钉事件订阅回调");
        log.debug(request.toString());
        log.debug(payload.toString());
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            /*
             * DingCallbackCrypto第三个参数 说明：
             * 1、开发者后台配置的订阅事件为应用级事件推送，此时OWNER_KEY为应用的APP_KEY。
             * 2、调用订阅事件接口订阅的事件为企业级事件推送，
             *     此时OWNER_KEY为：企业的appkey（企业内部应用）或SUITE_KEY（三方应用）
             */
            final String decryptMsg = decrypt(request, payload);
            log.debug("解密后的事件回调请求体:{}", decryptMsg);
            // 由于请求体跟事件类型相关, 不固定, 所以转Map取事件类型
            final Map<String, Object> decryptedMap = new GsonBuilder().create().fromJson(decryptMsg, Map.class);
            final String eventType = (String) decryptedMap.get("EventType");
            final Optional<DingMiniH5Event> eventOptional = DingMiniH5Event.of(eventType);
            Preconditions.checkArgument(eventOptional.isPresent(), "该插件模块暂不支持处理该类钉钉事件:" + eventType);
            final DingMiniH5Event miniH5EventType = eventOptional.get();
            if (Objects.equals(DingMiniH5Event.CheckEvent.CHECK_URL, miniH5EventType)) {
                // 测试回调url的正确性
                log.info(miniH5EventType.getName());
            } else {
                // 添加其他已注册的
                log.info("发生了:{}({})事件, 开始广播事件", miniH5EventType.getCode(), miniH5EventType.getName());
                eventPublisher.publishEvent(new DingMiniH5CallbackEvents.DingMiniH5EventDecryptedPayload(decryptMsg, miniH5EventType));
            }
            Map<String, String> successMap = callbackCrypto.getEncryptedMap("success");
            stopwatch.stop();
            log.debug("钉钉事件订阅回调耗时: {}ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
            return successMap;
        } catch (DingCallbackCrypto.DingTalkEncryptException e) {
            log.error("钉钉事件订阅回调失败 \n {} \n {} \n", request, payload, e);
            return null;
        }
    }
}
