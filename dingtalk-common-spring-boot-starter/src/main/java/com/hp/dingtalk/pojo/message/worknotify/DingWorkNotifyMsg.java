package com.hp.dingtalk.pojo.message.worknotify;

import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * 简化调用，原SDK提供的Msg对象容易将msgType和msg的对应关系搞乱
 *
 * @author hp 2023/3/22
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DingWorkNotifyMsg implements IDingWorkNotifyMsg {

    @SerializedName("msgtype")
    @Setter(AccessLevel.PROTECTED)
    private String msgType;

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.ActionCard actionCard) {
        Preconditions.checkArgument(Objects.nonNull(actionCard));
        this.actionCard = actionCard;
        this.msgType = "action_card";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.File file) {
        Preconditions.checkArgument(Objects.nonNull(file));
        this.file = file;
        this.msgType = "file";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.Image image) {
        Preconditions.checkArgument(Objects.nonNull(image));
        this.image = image;
        this.msgType = "image";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.Link link) {
        Preconditions.checkArgument(Objects.nonNull(link));
        this.link = link;
        this.msgType = "link";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown) {
        Preconditions.checkArgument(Objects.nonNull(markdown));
        this.markdown = markdown;
        this.msgType = "markdown";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.OA oa) {
        Preconditions.checkArgument(Objects.nonNull(oa));
        this.oa = oa;
        this.msgType = "oa";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.Text text) {
        Preconditions.checkArgument(Objects.nonNull(text));
        this.text = text;
        this.msgType = "text";
    }

    public DingWorkNotifyMsg(OapiMessageCorpconversationAsyncsendV2Request.Voice voice) {
        Preconditions.checkArgument(Objects.nonNull(voice));
        this.voice = voice;
        this.msgType = "voice";
    }

    /**
     * 卡片消息
     */
    @SerializedName("action_card")
    private OapiMessageCorpconversationAsyncsendV2Request.ActionCard actionCard;
    /**
     * 文件消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.File file;
    /**
     * 图片消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.Image image;
    /**
     * 链接消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.Link link;
    /**
     * markdown消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.Markdown markdown;
    /**
     * OA消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.OA oa;
    /**
     * 文本消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.Text text;
    /**
     * 语音消息
     */
    private OapiMessageCorpconversationAsyncsendV2Request.Voice voice;
}
