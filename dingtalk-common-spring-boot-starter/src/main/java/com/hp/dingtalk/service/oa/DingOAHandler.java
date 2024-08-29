package com.hp.dingtalk.service.oa;

import com.aliyun.dingtalkworkflow_1_0.Client;
import com.aliyun.dingtalkworkflow_1_0.models.QuerySchemaByProcessCodeHeaders;
import com.aliyun.dingtalkworkflow_1_0.models.QuerySchemaByProcessCodeRequest;
import com.aliyun.dingtalkworkflow_1_0.models.QuerySchemaByProcessCodeResponse;
import com.aliyun.dingtalkworkflow_1_0.models.QuerySchemaByProcessCodeResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingMiniH5;
import com.hp.dingtalk.component.exception.DingApiException;
import com.hp.dingtalk.pojo.oa.CreateProcessInstanceRequest;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

/**
 * @author hp
 */
@Slf4j
public class DingOAHandler extends AbstractDingApiHandler implements IDingOAHandler {

    public DingOAHandler(IDingMiniH5 app) {
        super(app);
    }

    @Override
    public String createProcessInstance(@NonNull CreateProcessInstanceRequest request) {
        final OapiProcessinstanceCreateRequest req = request.request();
        req.setAgentId(this.app.getAppId());
        OapiProcessinstanceCreateResponse response = execute(
                DingUrlConstant.OA.CREATE_PROCESS_INSTANCE,
                req,
                () -> "创建审批实例"
        );
        return response.getProcessInstanceId();
    }

    @Override
    public List<OapiProcessTemplateManageGetResponse.ProcessSimpleVO> getManageableProcessTemplatesInCorp(@NonNull String userId) {
        OapiProcessTemplateManageGetRequest request = new OapiProcessTemplateManageGetRequest();
        request.setUserid(userId);
        OapiProcessTemplateManageGetResponse response = execute(
                DingUrlConstant.OA.GET_MANAGEABLE_PROCESS_TEMPLATE_IN_CORP_LIST,
                request,
                () -> "获取企业内用户可管理的审批模版列表"
        );
        return response.getResult();
    }

    @Override
    public String getProcessTemplateCodeByName(@NonNull String name) {
        OapiProcessGetByNameRequest request = new OapiProcessGetByNameRequest();
        request.setName(name);
        OapiProcessGetByNameResponse response = execute(
                DingUrlConstant.OA.GET_PROCESS_TEMPLATE_CODE_BY_NAME,
                request,
                () -> "根据审批模版名称获取审批模版编码"
        );
        return response.getProcessCode();
    }

    @Override
    public OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start) {
        return getProcessInstanceIds(processCode, start, 10L, 0L);
    }

    @Override
    public OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start, @NonNull Long size, @NonNull Long cursor) {
        return getProcessInstanceIds(processCode, start, LocalDate.now(), size, cursor);
    }

    @Override
    public OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start, LocalDate end, @NonNull Long size, @NonNull Long cursor) {
        return getProcessInstanceIds(processCode, start, end, size, cursor, null);
    }

    @Override
    public OapiProcessinstanceListidsResponse.PageResult getProcessInstanceIds(@NonNull String processCode, @NonNull LocalDate start, LocalDate end, @NonNull Long size, @NonNull Long cursor, List<String> userIds) {
        Preconditions.checkArgument(0 < size && size <= 20, "分页参数，每页大小，最多传20");
        end = end == null ? LocalDate.now() : end;
        Preconditions.checkArgument(!end.minusDays(120L).isAfter(start), "时间范围不能超过120天");
        Preconditions.checkArgument(!LocalDate.now().minusDays(365L).isAfter(start), "start时间距当前时间不能超过365天");
        OapiProcessinstanceListidsRequest request = new OapiProcessinstanceListidsRequest();
        request.setProcessCode(processCode);
        request.setStartTime(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        request.setEndTime(end.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        request.setSize(size);
        request.setCursor(cursor);
        request.setUseridList(CollectionUtils.isEmpty(userIds) ? null : String.join(",", userIds));
        final OapiProcessinstanceListidsResponse response = execute(
                DingUrlConstant.OA.GET_PROCESS_INSTANCE_IDS,
                request,
                () -> "获取审批模版实例id列表"
        );
        return response.getResult();
    }

    @Override
    public OapiProcessinstanceGetResponse.ProcessInstanceTopVo getProcessInstance(@NonNull String processInstanceId) {
        OapiProcessinstanceGetRequest request = new OapiProcessinstanceGetRequest();
        request.setProcessInstanceId(processInstanceId);
        final OapiProcessinstanceGetResponse response = execute(
                DingUrlConstant.OA.GET_PROCESS_INSTANCE,
                request,
                () -> "获取审批实例"
        );
        return response.getProcessInstance();
    }

    @Override
    public QuerySchemaByProcessCodeResponseBody.QuerySchemaByProcessCodeResponseBodyResult getProcessTemplateSchema(String processCode) {
        QuerySchemaByProcessCodeHeaders headers = new QuerySchemaByProcessCodeHeaders();
        headers.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        QuerySchemaByProcessCodeRequest request = new QuerySchemaByProcessCodeRequest()
                .setProcessCode(processCode);
        final QuerySchemaByProcessCodeResponse response = execute(
                Client.class,
                client -> {
                    try {
                        return client.querySchemaByProcessCodeWithOptions(request, headers, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("获取OA审批表单schema失败", e);
                        throw new DingApiException("获取OA审批表单schema失败");
                    }
                },
                () -> "获取OA审批表单schema"
        );
        return response.getBody().getResult();
    }
}
