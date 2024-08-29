package com.hp.dingtalk.service;

import com.dingtalk.api.response.OapiRoleListResponse;
import com.dingtalk.api.response.OapiRoleSimplelistResponse;
import lombok.NonNull;

import java.util.List;

/**
 * 钉钉角色
 *
 * @author hp
 */
public interface IDingRoleHandler {

    List<OapiRoleListResponse.OpenRole> roles(@NonNull Long page, @NonNull Long size);

    List<OapiRoleSimplelistResponse.OpenEmpSimple> usersByRoleId(@NonNull Long roleId, @NonNull Long page,@NonNull Long size);
}
