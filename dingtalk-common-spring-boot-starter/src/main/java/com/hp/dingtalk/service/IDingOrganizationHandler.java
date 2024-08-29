package com.hp.dingtalk.service;

import com.aliyun.dingtalkcontact_1_0.models.GetOrgAuthInfoResponseBody;

/**
 * @author hp
 */
public interface IDingOrganizationHandler {

    GetOrgAuthInfoResponseBody getAuthInfo();
}
