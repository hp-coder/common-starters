package com.hp.dingtalk.pojo.message.webhook;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author hp
 */
public interface DingFeedCardMsg extends IDingBotWebhookMsg {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class FeedCard implements DingFeedCardMsg {
        List<FeedCardLink> links;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class FeedCardLink {

        String title;

        @SerializedName("messageURL")
        String messageUrl;

        @SerializedName("picURL")
        String picUrl;
    }
}
