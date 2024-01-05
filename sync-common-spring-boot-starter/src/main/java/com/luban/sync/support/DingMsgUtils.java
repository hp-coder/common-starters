package com.luban.sync.support;

import com.luban.dingtalk.component.application.IDingBot;
import com.luban.dingtalk.pojo.message.bot.IDingBotMsg;
import com.luban.dingtalk.service.message.DingBotMessageHandler;
import com.luban.sync.config.SyncConfig;
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

    public static void sendMsgByMobile(IDingBotMsg msg) {
        Assert.notNull(applicationContext, "spring 上下文为空");
        Assert.notNull(msg, "消息不能为空");
        new DingBotMessageHandler(applicationContext.getBean(IDingBot.class))
                .sendToUserByPhones(applicationContext.getBean(SyncConfig.class).getDingTalk().getDingTalkNotifyMobile(), msg);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        DingMsgUtils.applicationContext = applicationContext;
    }
}
