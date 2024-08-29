package com.hp.dingtalk.service;

import lombok.NonNull;

/**
 * 群组相关
 *
 * @author hp
 */
public interface IDingChatHandler {

    /**
     * 根据群chatId获取openConversationId
     *
     * @param chatId 会员id —> 后期钉钉将废弃该参数，使用openConversationId替代
     * @return openConversationId
     */
    String getOpenConversationIdByChatId(@NonNull String chatId);
}
