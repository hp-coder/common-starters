package com.hp.dingtalk.pojo.callback;

import com.google.gson.Gson;
import com.hp.common.base.annotation.FieldDesc;
import com.hp.common.base.model.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 通过配置机器人的消息链接,
 * 当在群里或单聊时向机器人@或发送消息
 * 钉钉发送的一个消息请求体
 * 更新后支持了更多类型的消息
 *
 * @author HP
 */
@Data
public class DingBotMsgCallbackRequest implements Request {

    @FieldDesc("会话ID")
    private String conversationId;

    @FieldDesc("1：单聊 2：群聊")
    private String conversationType;

    @FieldDesc("群聊时才有的会话标题。")
    private String conversationTitle;

    @FieldDesc("加密的机器人所在的企业corpId。")
    private String chatbotCorpId;

    @FieldDesc("加密的机器人ID。")
    private String chatbotUserId;

    private String robotCode;

    @FieldDesc("是否在@列表中。")
    private Boolean isInAtList;

    @FieldDesc("被@人的信息。")
    private List<AtUser> atUsers;

    @FieldDesc("加密的消息ID。")
    private String msgId;

    @FieldDesc("是否为管理员。")
    private Boolean isAdmin;

    @FieldDesc("消息的时间戳，单位毫秒。")
    private Long createAt;

    @FieldDesc("使用senderStaffId，作为发送者userid值。")
    private String senderStaffId;

    @FieldDesc("发送者昵称。")
    private String senderNick;

    @FieldDesc("企业内部群有的发送者当前群的企业corpId")
    private String senderCorpId;

    @FieldDesc("加密的发送者ID")
    private String senderId;

    @FieldDesc("当前会话的Webhook地址。")
    private String sessionWebhook;

    @FieldDesc("当前会话的Webhook地址过期时间。")
    private Long sessionWebhookExpiredTime;

    @FieldDesc("消息文本")
    private Text text;

    @FieldDesc("消息体，支持其他媒体类型")
    private Object content;

    @FieldDesc("消息类型。")
    private String msgtype;

    public <T> T getContent(Class<T> clazz) {
        final Gson gson = new Gson();
        return gson.fromJson(gson.toJson(this.content), clazz);
    }

    public boolean emptyContent() {
        return !Objects.nonNull(this.content);
    }

    private Object getContent() {
        return getContent(String.class);
    }

    @Data
    @NoArgsConstructor
    public static class Text {
        @FieldDesc("文字内容")
        String content;

        public Text(String content) {
            this.content = Optional.ofNullable(content).map(String::trim).orElse(null);
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Audio {
        @FieldDesc("语音的时长，单位是毫秒。")
        Integer duration;
        @FieldDesc("语音文件的下载码，用于换取下载语音的二进制文件。")
        String downloadCode;
        @FieldDesc("语音识别后的文本")
        String recognition;
    }

    @Data
    public static class Picture {
        @FieldDesc("图片文件的下载码，用于换取下载图片的二进制文件。")
        String downloadCode;
    }

    @Data
    public static class Video {
        @FieldDesc("视频的时长，单位是毫秒")
        Integer duration;
        @FieldDesc("视频文件的下载码，用于换取下载视频的二进制文件。")
        String downloadCode;
        @FieldDesc("视频文件类型。")
        String videoType;
    }

    @Data
    public static class File {
        @FieldDesc("文件的下载码，用于换取下载文件的二进制文件。")
        String downloadCode;
        @FieldDesc("文件名。")
        String fileName;
    }

    @Data
    public static class RichText {
        List<RichTextContent> richText;
    }

    @Data
    public static class RichTextContent {
        @FieldDesc("文本消息")
        RichTextContentText text;
        @FieldDesc("图片消息")
        RichTextContentPicture picture;
    }

    @Data
    public static class RichTextContentText {
        String text;
    }

    @Data
    public static class RichTextContentPicture {
        @FieldDesc("文件的下载码，用于换取下载文件的二进制文件。")
        String downloadCode;
        String type;
    }

    @Data
    public static class AtUser {
        @FieldDesc("加密的发送者ID。")
        String dingtalkId;
        @FieldDesc(" 企业内部群有的发送者在企业内的userid。")
        String staffId;
    }
}
