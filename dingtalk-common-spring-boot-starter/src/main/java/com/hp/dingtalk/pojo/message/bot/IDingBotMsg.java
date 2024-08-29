package com.hp.dingtalk.pojo.message.bot;

import com.google.gson.GsonBuilder;
import com.hp.dingtalk.pojo.GsonBuilderVisitor;
import com.hp.dingtalk.pojo.message.IDingMsg;
import com.taobao.api.internal.util.StringUtils;

import java.util.Optional;

/**
 * Messages that can be only sent by IDingBot.
 *
 * @author hp 2023/3/17
 */
public interface IDingBotMsg extends IDingMsg<String> {

    /**
     * <a href="https://open.dingtalk.com/document/isvapp/the-robot-sends-queries-and-withdraws-single-chat-messages">机器人发送消息的类型</a>
     *
     * @return msgType
     */
    @Override
    default String getMsgType() {
        return StringUtils.toCamelStyle(this.getClass().getSimpleName());
    }

    /**
     * <a href="https://open.dingtalk.com/document/isvapp/the-robot-sends-queries-and-withdraws-single-chat-messages">机器人发送消息的类型</a>
     *
     * @return json message
     */
    @Override
    default String getMsg() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        Optional.ofNullable(getGsonBuilderVisitor())
                .ifPresent(visitor -> visitor.customize(gsonBuilder));
        return gsonBuilder.create().toJson(this);
    }

    /**
     * @return gson serializer customizer
     */
    default GsonBuilderVisitor getGsonBuilderVisitor() {
        return GsonBuilder::disableHtmlEscaping;
    }
}
