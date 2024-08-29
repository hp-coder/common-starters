package com.hp.dingtalk.pojo.message.worknotify;

import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.hp.dingtalk.pojo.GsonBuilderVisitor;
import com.hp.dingtalk.pojo.message.IDingMsg;
import com.taobao.api.internal.mapping.ApiField;

import java.util.Optional;

/**
 * 消息字段需要消息类型和以消息类型为key的消息内容组成的值
 * <p>
 * <p>
 * {@code { "msgtype": "text", "text": { "content": "请提交日报。" } }}
 * <p>
 * <a href="https://open.dingtalk.com/document/orgapp/asynchronous-sending-of-enterprise-session-messages">钉钉-工作通知文档</a>
 * <p>
 * 文档写得太偷懒了，可以从OapiMessageCorpconversationAsyncsendV2Request看到，
 * msg字段直接将OapiMessageCorpconversationAsyncsendV2Request.Msg无脑json序列化，
 * 但是Msg.msgType字段只是最后set的那个，其实没有强对应关系，如果传递的msgtype和实际的msg对象不同，消息将发送失败
 *
 * @author hp 2023/3/21
 */
public interface IDingWorkNotifyMsg extends IDingMsg<String> {

    /**
     * Message in JSON format
     *
     * @return JSON
     */
    @Override
    default String getMsg() {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        Optional.ofNullable(getGsonBuilderVisitor())
                .ifPresent(visitor -> visitor.customize(gsonBuilder));
        return gsonBuilder.create()
                .toJson(this);
    }

    /**
     * @return json key
     * @see OapiMessageCorpconversationAsyncsendV2Request
     */
    default GsonBuilderVisitor getGsonBuilderVisitor() {
        return builder -> {
            final FieldNamingStrategy strategy = f -> {
                if (f.isAnnotationPresent(SerializedName.class)) {
                    return f.getAnnotation(SerializedName.class).value();
                }
                if (f.isAnnotationPresent(ApiField.class)) {
                    return f.getAnnotation(ApiField.class).value();
                }
                return FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES.translateName(f);
            };
            builder.setFieldNamingStrategy(strategy);
            builder.disableHtmlEscaping();
        };
    }
}
