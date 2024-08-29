package com.hp.dingtalk.controller;


import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.hp.dingtalk.pojo.callback.DingBotMsgCallbackRequest;
import com.hp.dingtalk.service.IDingBotMsgCallBackHandler;
import com.hp.dingtalk.component.application.IDingBot;
import com.hp.dingtalk.component.factory.app.DingAppFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 钉钉机器人消息互动
 *
 * @author hp
 */
@Slf4j
@RestController
@RequestMapping("/ding")
public class DingBotMsgCallbackController extends AbstractDingBotMsgCallbackController {

    public DingBotMsgCallbackController(List<IDingBotMsgCallBackHandler<?>> dingBotMsgCallBackHandlers) {
        super(dingBotMsgCallBackHandlers);
    }

    @SneakyThrows
    @RequestMapping("/bot/msg/callback")
    public void msg(
            @RequestHeader(value = "timestamp", required = false) String timeStamp,
            @RequestHeader(value = "sign", required = false) String sign,
            @RequestBody(required = false) DingBotMsgCallbackRequest payload
    ) {
        if (timeStamp == null && sign == null && payload ==null ){
            log.warn("The dingtalk robot callback availability checking request is received.");
            return;
        }
        log.debug("timestamp:{},sign:{},content:{}", timeStamp, sign, new Gson().toJson(payload));
        IDingBot bot = DingAppFactory.app(payload.getRobotCode());
        Preconditions.checkNotNull(bot, String.format("SpringContext中未找到对应的钉钉应用Bean: APP_KEY:%s", payload.getRobotCode()));
        validateRequest(timeStamp, sign, bot);
        handlers(bot, payload)
                .ifPresent(handlers -> handlers.parallelStream().forEachOrdered(handler -> handler.handle(bot, payload)));
    }

}
