package com.hp.dingtalk.service.message;


import com.aliyun.dingtalkim_1_0.Client;
import com.aliyun.dingtalkim_1_0.models.*;
import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOHeaders;
import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTORequest;
import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOResponse;
import com.aliyun.teautil.models.RuntimeOptions;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingBotMessageHandler;
import com.hp.dingtalk.service.IDingInteractiveMessageHandler;
import com.hp.dingtalk.service.user.DingUserHandler;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingBot;
import com.hp.dingtalk.component.exception.DingApiException;
import com.hp.dingtalk.pojo.message.bot.IDingBotMsg;
import com.hp.dingtalk.pojo.message.interactive.IDingInteractiveMsg;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hp
 */
@Slf4j
public class DingBotMessageHandler extends AbstractDingApiHandler implements IDingBotMessageHandler, IDingInteractiveMessageHandler {

    public DingBotMessageHandler(IDingBot app) {
        super(app);
    }

    @Override
    public BatchSendOTOResponse sendToUserByUserIds(@NonNull List<String> userIds, @NonNull IDingBotMsg msg) {
        BatchSendOTOHeaders headers = new BatchSendOTOHeaders();
        headers.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        BatchSendOTORequest request = new BatchSendOTORequest()
                .setRobotCode(app.getAppKey())
                .setUserIds(userIds)
                .setMsgKey(msg.getMsgType())
                .setMsgParam(msg.getMsg());
        final BatchSendOTOResponse response = execute(
                com.aliyun.dingtalkrobot_1_0.Client.class,
                client -> {
                    try {
                        return client.batchSendOTOWithOptions(request, headers, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("机器人向用户发送普通消息失败", e);
                        throw new DingApiException("机器人向用户发送普通消息失败");
                    }
                },
                () -> "机器人向用户发送普通消息");
        log.debug("机器人向用户发送普通消息响应:{}", response);
        return response;
    }

    @Override
    public BatchSendOTOResponse sendToUserByPhones(@NonNull List<String> mobiles, @NonNull IDingBotMsg msg) {
        final DingUserHandler handler = new DingUserHandler(app);
        final List<String> userIds = mobiles
                .stream()
                .map(handler::findUserIdByMobile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return sendToUserByUserIds(userIds, msg);
    }

    @Override
    public String sendInteractiveMsgToIndividual(@NotNull List<String> userIds, @NotNull IDingInteractiveMsg interactiveMsg) {
        SendInteractiveCardHeaders sendInteractiveCardHeaders = new SendInteractiveCardHeaders();
        sendInteractiveCardHeaders.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        SendInteractiveCardRequest.SendInteractiveCardRequestCardData cardData = new SendInteractiveCardRequest.SendInteractiveCardRequestCardData();
        cardData.setCardParamMap(interactiveMsg.getMsg());
        SendInteractiveCardRequest sendInteractiveCardRequest = new SendInteractiveCardRequest()
                .setCardTemplateId(interactiveMsg.getTemplateId())
                .setReceiverUserIdList(userIds)
                .setConversationType(0)
                .setCallbackRouteKey(interactiveMsg.getCallbackRouteKey())
                .setCardData(cardData)
                .setOutTrackId(interactiveMsg.getOutTrackId());
        final SendInteractiveCardResponse response = execute(
                Client.class,
                client -> {
                    try {
                        return client.sendInteractiveCardWithOptions(sendInteractiveCardRequest, sendInteractiveCardHeaders, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("机器人发送互动卡片高级版至个人失败", e);
                        throw new DingApiException("机器人发送互动卡片高级版至个人失败");
                    }
                },
                () -> "机器人发送互动卡片高级版至个人"
        );
        final String processQueryKey = response.getBody().getResult().getProcessQueryKey();
        log.debug("机器人发送互动卡片高级版至个人实例id:{}", processQueryKey);
        return interactiveMsg.getOutTrackId();
    }

    @Deprecated
    @Override
    public String sendInteractiveMsgToGroup(List<String> userIds, String openConversationId, IDingInteractiveMsg interactiveMsg) {
        SendInteractiveCardHeaders sendInteractiveCardHeaders = new SendInteractiveCardHeaders();
        sendInteractiveCardHeaders.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        SendInteractiveCardRequest.SendInteractiveCardRequestCardData cardData = new SendInteractiveCardRequest.SendInteractiveCardRequestCardData();
        cardData.setCardParamMap(interactiveMsg.getMsg());
        SendInteractiveCardRequest sendInteractiveCardRequest = new SendInteractiveCardRequest()
                .setRobotCode(app.getAppKey())
                .setOpenConversationId(openConversationId)
                .setCardTemplateId(interactiveMsg.getTemplateId())
                .setReceiverUserIdList(userIds)
                .setConversationType(1)
                .setCallbackRouteKey(interactiveMsg.getCallbackRouteKey())
                .setCardData(cardData)
                .setOutTrackId(interactiveMsg.getOutTrackId());

        final SendInteractiveCardResponse response = execute(
                Client.class,
                client -> {
                    try {
                        return client.sendInteractiveCardWithOptions(sendInteractiveCardRequest, sendInteractiveCardHeaders, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("机器人发送互动卡片高级版至群聊失败");
                        throw new DingApiException("机器人发送互动卡片高级版至群聊失败");
                    }
                },
                () -> "机器人发送互动卡片高级版至群聊"
        );
        final String processQueryKey = response.getBody().getResult().getProcessQueryKey();
        log.debug("机器人发送互动卡片高级版至群聊实例id:{}", processQueryKey);
        return interactiveMsg.getOutTrackId();
    }

    @Override
    public String updateInteractiveMsg(@NotNull @Nullable String openConversationId, @NotNull IDingInteractiveMsg interactiveMsg) {
        UpdateInteractiveCardHeaders updateInteractiveCardHeaders = new UpdateInteractiveCardHeaders();
        updateInteractiveCardHeaders.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        UpdateInteractiveCardRequest.UpdateInteractiveCardRequestCardOptions cardOptions = new UpdateInteractiveCardRequest.UpdateInteractiveCardRequestCardOptions()
                .setUpdateCardDataByKey(true)
                .setUpdatePrivateDataByKey(true);
        UpdateInteractiveCardRequest.UpdateInteractiveCardRequestCardData cardData = new UpdateInteractiveCardRequest.UpdateInteractiveCardRequestCardData()
                .setCardParamMap(interactiveMsg.getMsg());
        UpdateInteractiveCardRequest updateInteractiveCardRequest = new UpdateInteractiveCardRequest()
                .setOutTrackId(interactiveMsg.getOutTrackId())
                .setCardData(cardData).setUserIdType(1)
                .setCardOptions(cardOptions);
        execute(
                Client.class,
                client -> {
                    try {
                        return client.updateInteractiveCardWithOptions(updateInteractiveCardRequest, updateInteractiveCardHeaders, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("机器人更新互动卡片高级版失败", e);
                        throw new DingApiException("机器人更新互动卡片高级版失败");
                    }
                },
                () -> "机器人更新互动卡片高级版"
        );
        return interactiveMsg.getOutTrackId();
    }
}
