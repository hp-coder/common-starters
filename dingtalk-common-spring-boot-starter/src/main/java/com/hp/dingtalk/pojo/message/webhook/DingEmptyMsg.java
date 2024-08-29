package com.hp.dingtalk.pojo.message.webhook;

import lombok.Getter;

/**
 * @author hp
 */
public interface DingEmptyMsg extends IDingBotWebhookMsg {

    @Getter
    class EmptyMsg implements DingEmptyMsg {

        private final String msgType = "empty";

    }
}

