package com.hp.dingtalk.component;

import cn.hutool.core.collection.CollUtil;
import com.hp.dingtalk.component.application.IDingBot;
import com.hp.dingtalk.component.factory.app.DingAppFactory;
import com.hp.dingtalk.pojo.message.interactive.callback.IDingInteractiveCardCallBack;
import com.hp.dingtalk.service.callback.interactivemsg.DingInteractiveMessageCallbackRegisterHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author hp
 */
@Async
@Slf4j
@RequiredArgsConstructor
public class DingBooter implements ApplicationListener<ApplicationReadyEvent> {

    private final List<IDingInteractiveCardCallBack> callBacks;

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent applicationReadyEvent) {
        if (CollUtil.isEmpty(callBacks)) {
            return;
        }
        callBacks.forEach(callBack ->
                callBack.getDingBots()
                        .forEach(clazz -> {
                            final IDingBot app = DingAppFactory.app(clazz);
                            final DingInteractiveMessageCallbackRegisterHandler registerHandler = new DingInteractiveMessageCallbackRegisterHandler(app);
                            registerHandler.registerCallBackUrl(callBack, true);
                        })
        );
    }
}
