package com.hp.dingtalk.pojo.message.bot;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author hp
 */
public interface DingLinkMsg extends IDingBotMsg {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    class SampleLink implements DingLinkMsg {
        String messageUrl;
        String picUrl;
        String title;
        String text;
    }
}
