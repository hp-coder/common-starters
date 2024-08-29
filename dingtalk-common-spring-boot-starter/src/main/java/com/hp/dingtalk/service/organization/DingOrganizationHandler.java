package com.hp.dingtalk.service.organization;

import com.aliyun.dingtalkcontact_1_0.Client;
import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoHeaders;
import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoRequest;
import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoResponse;
import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoResponseBody;
import com.aliyun.teautil.models.RuntimeOptions;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.component.SDK;
import com.hp.dingtalk.component.application.IDingMiniH5;
import com.hp.dingtalk.component.exception.DingApiException;
import com.hp.dingtalk.service.IDingOrganizationHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hp
 */
@Slf4j
public class DingOrganizationHandler extends AbstractDingApiHandler implements IDingOrganizationHandler {

    public DingOrganizationHandler(@NonNull IDingMiniH5 app) {
        super(app);
    }

    public GetOrgAuthInfoResponseBody getAuthInfo() {
        GetOrgAuthInfoHeaders headers = new GetOrgAuthInfoHeaders();
        headers.xAcsDingtalkAccessToken = getAccessToken(SDK.Version.NEW);
        GetOrgAuthInfoRequest request = new GetOrgAuthInfoRequest();
        final GetOrgAuthInfoResponse execute = execute(
                Client.class,
                client -> {
                    try {
                        return client.getOrgAuthInfoWithOptions(request, headers, new RuntimeOptions());
                    } catch (Exception e) {
                        log.error("获取企业认证信息失败", e);
                        throw new DingApiException("获取企业认证信息失败");
                    }
                },
                () -> "获取企业认证信息"
        );
        return execute.getBody();
    }
}
