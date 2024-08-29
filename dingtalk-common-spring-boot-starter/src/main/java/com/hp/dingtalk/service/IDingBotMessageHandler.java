package com.hp.dingtalk.service;


import com.aliyun.dingtalkrobot_1_0.models.BatchSendOTOResponse;
import com.hp.dingtalk.pojo.message.bot.IDingBotMsg;
import lombok.NonNull;

import java.util.List;

/**
 * 钉钉机器人消息处理器
 *
 * @author hp
 */
public interface IDingBotMessageHandler {

    /**
     * 通过手机号查找钉钉用户并发送消息
     *
     * @param userIds 钉钉用户id
     * @param msg     IDingBotMsg
     * @return
     */
    BatchSendOTOResponse sendToUserByUserIds(@NonNull List<String> userIds, @NonNull IDingBotMsg msg);

    /**
     * 通过手机号查找钉钉用户并发送消息
     *
     * @param mobiles 手机号 无前缀+86等
     * @param msg     IDingBotMsg
     */
    BatchSendOTOResponse sendToUserByPhones(@NonNull List<String> mobiles, @NonNull IDingBotMsg msg);

}
