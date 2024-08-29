package com.hp.dingtalk.service.role;

import com.dingtalk.api.request.OapiRoleListRequest;
import com.dingtalk.api.request.OapiRoleSimplelistRequest;
import com.dingtalk.api.response.OapiRoleListResponse;
import com.dingtalk.api.response.OapiRoleSimplelistResponse;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingRoleHandler;
import com.hp.dingtalk.component.application.IDingApp;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 钉钉角色
 *
 * @author hp
 */
@Slf4j
public class DingRoleHandler extends AbstractDingApiHandler implements IDingRoleHandler {

    public DingRoleHandler(@NonNull IDingApp app) {
        super(app);
    }

    @Override
    public List<OapiRoleListResponse.OpenRole> roles(@NonNull Long page, @NonNull Long size) {
        page = page <= 0 ? 1 : page;
        size = size <= 0 || size > 200 ? 200 : size;
        OapiRoleListRequest request = new OapiRoleListRequest();
        request.setSize(size);
        request.setOffset((page - 1) * size);
        final OapiRoleListResponse response = execute(DingUrlConstant.Role.GET_ROLE_LIST, request, () -> "获取角色列表");
        return response.getResult()
                .getList()
                .stream()
                .map(OapiRoleListResponse.OpenRoleGroup::getRoles)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public List<OapiRoleSimplelistResponse.OpenEmpSimple> usersByRoleId(@NonNull Long roleId, @NonNull Long page, @NonNull Long size) {
        page = page <= 0 ? 1 : page;
        size = size <= 0 || size > 100 ? 100 : size;
        OapiRoleSimplelistRequest request = new OapiRoleSimplelistRequest();
        request.setRoleId(roleId);
        request.setSize(size);
        request.setOffset((page - 1) * size);
        OapiRoleSimplelistResponse response = execute(DingUrlConstant.Role.GET_USERS_BY_ROLE_ID, request, () -> "根据角色Id获取用户列表");
        return response.getResult().getList();
    }
}
