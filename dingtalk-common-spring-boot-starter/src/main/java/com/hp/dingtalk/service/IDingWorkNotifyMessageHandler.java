package com.hp.dingtalk.service;

import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendprogressResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationStatusBarUpdateResponse;
import com.hp.common.base.enums.BaseEnum;
import com.hp.dingtalk.pojo.message.worknotify.IDingWorkNotifyMsg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

/**
 * 工作通知发送处理器
 *
 * @author hp 2023/3/22
 */
public interface IDingWorkNotifyMessageHandler {

    /**
     * 相对常用一点
     *
     * @param userIds 接收者的userid列表，最大用户列表长度100
     * @param msg     工作通知，最长不超过2048个字节
     */
    OapiMessageCorpconversationAsyncsendV2Response sendWorkNotifyToUsers(@NonNull List<String> userIds, @NonNull IDingWorkNotifyMsg msg);

    /**
     * 发送工作通知
     *
     * @param userIds   接收者的userid列表，最大用户列表长度100
     * @param deptIds   接收者的部门id列表，最大列表长度20。接收者是部门ID时，包括子部门下的所有用户。
     * @param toAllUser 是否发送给企业全部用户，当设置为false时必须指定userid_list或dept_id_list其中一个参数的值。
     * @param msg       工作通知，最长不超过2048个字节
     */
    OapiMessageCorpconversationAsyncsendV2Response sendWorkNotify(List<String> userIds, List<String> deptIds, boolean toAllUser, @NonNull IDingWorkNotifyMsg msg);

    /**
     * 获取工作通知发送进度
     *
     * @param taskId 工作通知实例id, 由发送接口返回
     * @return 工作通知发送进度
     */
    OapiMessageCorpconversationGetsendprogressResponse getWorkNotifyProgress(@NonNull Long taskId);

    /**
     * 获取工作通知发送结果
     *
     * @param taskId 工作通知实例id, 由发送接口返回
     * @return 工作通知发送结果
     */
    OapiMessageCorpconversationGetsendresultResponse getWorkNotifyResult(@NonNull Long taskId);


    /**
     * 当且仅当使用发送工作通知接口发送OA消息通知时指定了status_bar参数。
     * 调用本接口更新OA工作通知的消息状态。
     * <p>
     * <a href="https://open.dingtalk.com/document/orgapp/work-notice-option#96bb77ff3a04i">更新工作通知状态栏</a>
     *
     * @param taskId        工作通知实例id, 由发送接口返回
     * @param statusValue   状态栏值。
     * @param statusBgColor 状态栏背景色，推荐0xFF加六位颜色值。
     * @return
     */
    OapiMessageCorpconversationStatusBarUpdateResponse updateWorkNotify(@NonNull Long taskId, @NonNull String statusValue, @NonNull StatusBgColor statusBgColor);


    interface StatusBgColor {
        /**
         * @return 0xFF加六位颜色值
         */
        int getColor();
    }


    @Getter
    @AllArgsConstructor
    enum SuggestColor implements BaseEnum<SuggestColor, Integer>, StatusBgColor {
        /***/
        APPROVED(0xFF78C06E, "已同意"),
        DENIED(0xFFF65E5E, "已拒绝"),
        WITHDRAW(0xFF858E99, "已撤销"),
        PENDING(0xFFFF9D46, "待审批"),
        ;

        private final Integer code;
        private final String name;

        /**
         * 获取颜色值
         *
         * @return 0xFF加六位颜色值
         */
        @Override
        public int getColor() {
            return getCode();
        }
    }
}
