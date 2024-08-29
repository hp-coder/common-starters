package com.hp.dingtalk.pojo.message.webhook;

import com.google.gson.GsonBuilder;
import com.hp.dingtalk.pojo.GsonBuilderVisitor;
import com.hp.dingtalk.pojo.message.IDingMsg;
import com.taobao.api.internal.util.StringUtils;

import java.util.Optional;

/**
 * It's not safe to use yet.
 * <p>
 * Not thoroughly tested.
 * <p>
 * action_card 类型 机器人和app的消息差异过大，目前选择实现机器人的
 * 大小写/驼峰问题：为了对应api文档字段
 * 人与机器人会话中机器人消息：支持图片、语音、文件收发能力。
 * 群聊会话中机器人消息：图片、语音、视频、文件发送能力，但群聊中用户无法@机器人发送语音、视频、文件给机器人。
 *
 * @author hp 2023/3/17
 */
public interface IDingBotWebhookMsg extends IDingMsg<String> {

    /**
     * @return camel cased msgType by default
     */
    @Override
    default String getMsgType() {
        return StringUtils.toCamelStyle(this.getClass().getSimpleName());
    }

    /**
     * @return message in JSON format
     */
    @Override
    default String getMsg() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        Optional.ofNullable(gsonCustomizer())
                .ifPresent(visitor -> visitor.customize(gsonBuilder));
        return gsonBuilder.create().toJson(this);
    }

    /**
     * Customize JSON serializer according to the actual request of the documentations.
     * <p>
     * Implement this method if needed.
     *
     * @return A visitor that customize the GsonBuilder.
     */
    default GsonBuilderVisitor gsonCustomizer() {
        return GsonBuilder::disableHtmlEscaping;
    }
}
