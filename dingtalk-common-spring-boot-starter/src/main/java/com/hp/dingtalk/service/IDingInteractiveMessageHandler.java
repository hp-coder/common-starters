package com.hp.dingtalk.service;

import com.hp.dingtalk.pojo.message.interactive.IDingInteractiveMsg;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 发送钉钉互动卡片，高级版，普通版暂不实现
 * 以下接口都是使用钉钉userId模式，不推荐unionId模式
 * <p>
 * unionid是员工在当前开发者企业账号范围内的唯一标识，由系统生成：
 * 同一个企业员工，在不同的开发者企业账号下，unionid是不相同的。
 * 在同一个开发者企业账号下，unionid是唯一且不变的，例如同一个服务商开发的多个应用，或者是扫码登录等场景的多个App账号。
 *
 * @author hp
 */
public interface IDingInteractiveMessageHandler {

    /**
     * 发送互动卡片至单聊
     * 机器人对用户单聊
     * 这里不对是否转发，针对某个用户的私域数据，@用户做处理
     *
     * @param userIds        接收用户
     * @param interactiveMsg 卡片消息：包含必要配置信息：卡片id，callbackUrl等
     * @return outTrackId for additional usage
     */
    String sendInteractiveMsgToIndividual(@NonNull List<String> userIds,@NonNull IDingInteractiveMsg interactiveMsg);

    /**
     * 发送互动卡片至群聊
     *
     * @param userIds            为空则群内所有成员可见，不为空则指定人员可见
     * @param openConversationId 场景群id
     * @param interactiveMsg     卡片消息：包含必要配置信息：卡片id，callbackUrl等
     * @return outTrackId
     * @deprecated 还没仔细验证过群聊场景，验证后再逐渐增加业务常用参数，近期业务用不到群聊场景，暂时不更新
     */
    @Deprecated
    String sendInteractiveMsgToGroup(List<String> userIds, String openConversationId, IDingInteractiveMsg interactiveMsg);

    /**
     * 更新互动卡片
     *
     * @param openConversationId 场景群id
     * @param interactiveMsg     卡片消息：包含必要配置信息：卡片id，callbackUrl等
     * @return 互动卡片唯一id（outTrackId）
     */
    String updateInteractiveMsg(@Nullable String openConversationId, @NonNull IDingInteractiveMsg interactiveMsg);
}
