package com.hp.dingtalk.service.message;


import cn.hutool.core.collection.CollUtil;
import com.dingtalk.api.request.OapiMessageCorpconversationAsyncsendV2Request;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendprogressRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationGetsendresultRequest;
import com.dingtalk.api.request.OapiMessageCorpconversationStatusBarUpdateRequest;
import com.dingtalk.api.response.OapiMessageCorpconversationAsyncsendV2Response;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendprogressResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationGetsendresultResponse;
import com.dingtalk.api.response.OapiMessageCorpconversationStatusBarUpdateResponse;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingWorkNotifyMessageHandler;
import com.hp.common.base.utils.ParameterHelper.Collections;
import com.hp.dingtalk.component.application.IDingMiniH5;
import com.hp.dingtalk.pojo.message.worknotify.IDingWorkNotifyMsg;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author hp
 */
@Slf4j
public class DingWorkNotifyMessageHandler extends AbstractDingApiHandler implements IDingWorkNotifyMessageHandler {

    public DingWorkNotifyMessageHandler(IDingMiniH5 app) {
        super(app);
    }

    @Override
    public OapiMessageCorpconversationAsyncsendV2Response sendWorkNotifyToUsers(@NotNull List<String> userIds, @NotNull IDingWorkNotifyMsg msg) {
        return sendWorkNotify(userIds, null, false, msg);
    }

    @Override
    public OapiMessageCorpconversationAsyncsendV2Response sendWorkNotify(List<String> userIds, List<String> deptIds, boolean toAllUser, @NotNull IDingWorkNotifyMsg msg) {
        if (!toAllUser) {
            Preconditions.checkArgument(CollUtil.isNotEmpty(userIds) || CollUtil.isNotEmpty(deptIds),
                    "当toAllUser设置为false时必须指定userIds或deptIds其中一个参数的值。"
            );
        }
        OapiMessageCorpconversationAsyncsendV2Request request = new OapiMessageCorpconversationAsyncsendV2Request();
        if (!toAllUser) {
            request.setUseridList(CollUtil.isNotEmpty(userIds) ? String.join(",", userIds) : null);
            request.setDeptIdList(CollUtil.isNotEmpty(deptIds) ? String.join(",", deptIds) : null);
        }
        request.setAgentId(app.getAppId());
        request.setToAllUser(toAllUser);
        request.setMsg(msg.getMsg());
        Collections.ofNullable(deptIds).ifPresent(ids -> request.setDeptIdList(String.join(",", ids)));
        return execute(
                DingUrlConstant.WorkNotify.SEND_WORK_NOTIFY,
                request,
                () -> "发送工作通知"
        );
    }


    @Override
    public OapiMessageCorpconversationGetsendprogressResponse getWorkNotifyProgress(@NonNull Long taskId) {
        OapiMessageCorpconversationGetsendprogressRequest request = new OapiMessageCorpconversationGetsendprogressRequest();
        request.setAgentId(app.getAppId());
        request.setTaskId(taskId);
        return execute(
                DingUrlConstant.WorkNotify.GET_WORK_NOTIFY_PROGRESS,
                request,
                () -> "获取工作通知发送进度"
        );
    }

    @Override
    public OapiMessageCorpconversationGetsendresultResponse getWorkNotifyResult(@NonNull Long taskId) {
        OapiMessageCorpconversationGetsendresultRequest request = new OapiMessageCorpconversationGetsendresultRequest();
        request.setAgentId(app.getAppId());
        request.setTaskId(taskId);
        return execute(
                DingUrlConstant.WorkNotify.GET_WORK_NOTIFY_RESULT,
                request,
                () -> "获取工作通知发送结果"
        );
    }

    @Override
    public OapiMessageCorpconversationStatusBarUpdateResponse updateWorkNotify(@NonNull Long taskId, @NonNull String statusValue, @NonNull StatusBgColor statusBgColor) {
        OapiMessageCorpconversationStatusBarUpdateRequest request = new OapiMessageCorpconversationStatusBarUpdateRequest();
        request.setAgentId(app.getAppId());
        request.setStatusValue(statusValue);
        request.setStatusBg(String.valueOf(statusBgColor.getColor()));
        request.setTaskId(taskId);
        return execute(
                DingUrlConstant.WorkNotify.UPDATE_WORK_NOTIFY,
                request,
                () -> "更新工作通知"
        );
    }

}
