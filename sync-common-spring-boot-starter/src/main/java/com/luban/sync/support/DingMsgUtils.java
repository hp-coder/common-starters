package com.luban.sync.support;

import com.luban.sync.config.SyncConfig;
import com.luban.dingding.component.application.IDingBot;
import com.luban.dingding.pojo.message.IDingMsg;
import com.luban.dingding.service.message.DingBotMessageHandler;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * @Author: HP
 */
public class DingMsgUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static void sendMsgByMobile(IDingMsg msg) {
        Assert.notNull(applicationContext,"spring 上下文为空");
        Assert.notNull(msg, "消息不能为空");
        new DingBotMessageHandler()
                .sendMsgThroughMobile(
                        applicationContext.getBean(IDingBot.class),
                        applicationContext.getBean(SyncConfig.class).getDingTalk().getDingTalkNotifyMobile(),
                        null,
                        false,
                        msg
                );
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        DingMsgUtils.applicationContext = applicationContext;
    }
}
