package com.hp.dingtalk.pojo.message.bot;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

/**
 * @author hp
 */
public interface DingActionCardMsg extends IDingBotMsg {

    /**
     * 卡片消息：一个按钮
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleActionCard implements DingActionCardMsg {
        String title;
        String text;
        String singleTitle;
        @SerializedName("singleURL")
        String singleUrl;
    }

    /**
     * 卡片消息：竖向两个按钮
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleActionCard2 implements DingActionCardMsg {
        String title;
        String text;
        String actionTitle;
        @SerializedName("actionURL1")
        String actionUrl1;
        String actionTitle2;
        @SerializedName("actionURL2")
        String actionUrl2;
    }

    /**
     * 卡片消息：竖向三个按钮
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleActionCard3 implements DingActionCardMsg {
        String title;
        String text;
        String actionTitle;
        @SerializedName("actionURL1")
        String actionUrl1;
        String actionTitle2;
        @SerializedName("actionURL2")
        String actionUrl2;
        String actionTitle3;
        @SerializedName("actionURL3")
        String actionUrl3;
    }

    /**
     * 卡片消息：竖向四个按钮
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleActionCard4 implements DingActionCardMsg {
        String title;
        String text;
        String actionTitle;
        @SerializedName("actionURL1")
        String actionUrl1;
        String actionTitle2;
        @SerializedName("actionURL2")
        String actionUrl2;
        String actionTitle3;
        @SerializedName("actionURL3")
        String actionUrl3;
        String actionTitle4;
        @SerializedName("actionURL4")
        String actionUrl4;
    }

    /**
     * 卡片消息：竖向五个按钮
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleActionCard5 implements DingActionCardMsg {
        String title;
        String text;
        String actionTitle;
        @SerializedName("actionURL1")
        String actionUrl1;
        String actionTitle2;
        @SerializedName("actionURL2")
        String actionUrl2;
        String actionTitle3;
        @SerializedName("actionURL3")
        String actionUrl3;
        String actionTitle4;
        @SerializedName("actionURL4")
        String actionUrl4;
        String actionTitle5;
        @SerializedName("actionURL5")
        String actionUrl5;
    }

    /**
     * 卡片消息：横向二个按钮。
     */
     @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleActionCard6 implements DingActionCardMsg {
        String title;
        String text;
        String buttonTitle1;
        String buttonUrl1;
        String buttonTitle2;
        String buttonUrl2;
    }
}
