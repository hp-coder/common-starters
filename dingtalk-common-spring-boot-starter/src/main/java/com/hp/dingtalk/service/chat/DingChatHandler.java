package com.hp.dingtalk.service.chat;

import com.aliyun.dingtalkim_1_0.Client;
import com.aliyun.dingtalkim_1_0.models.ChatIdToOpenConversationIdHeaders;
import com.aliyun.dingtalkim_1_0.models.ChatIdToOpenConversationIdResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingChatHandler;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingApp;
import com.hp.dingtalk.component.exception.DingApiException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * 群组相关
 *
 * @author hp
 */
@Slf4j
public class DingChatHandler extends AbstractDingApiHandler implements IDingChatHandler {

    public DingChatHandler(IDingApp app) {
        super(app);
    }

    @Override
    public String getOpenConversationIdByChatId(@NonNull String chatId) {
        ChatIdToOpenConversationIdHeaders chatIdToOpenConversationIdHeaders = new ChatIdToOpenConversationIdHeaders();
        chatIdToOpenConversationIdHeaders.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        final ChatIdToOpenConversationIdResponse response = execute(
                Client.class,
                client -> {
                    try {
                        return client.chatIdToOpenConversationIdWithOptions(chatId, chatIdToOpenConversationIdHeaders, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("根据chatId获取getOpenConversationId", e);
                        throw new DingApiException("根据chatId获取getOpenConversationId", e);
                    }
                },
                () -> "根据chatId获取getOpenConversationId"
        );
        return response.getBody().getOpenConversationId();
    }
}
