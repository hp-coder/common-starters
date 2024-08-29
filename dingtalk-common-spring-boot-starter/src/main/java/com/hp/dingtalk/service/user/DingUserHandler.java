package com.hp.dingtalk.service.user;


import cn.hutool.core.lang.RegexPool;
import com.dingtalk.api.request.*;
import com.dingtalk.api.response.*;
import com.google.common.base.Preconditions;
import com.hp.dingtalk.constant.DeptUserOrder;
import com.hp.dingtalk.constant.DingUrlConstant;
import com.hp.dingtalk.constant.Language;
import com.hp.dingtalk.service.AbstractDingApiHandler;
import com.hp.dingtalk.service.IDingUserHandler;
import com.hp.dingtalk.component.application.IDingApp;
import com.hp.dingtalk.component.exception.DingApiException;
import com.hp.dingtalk.utils.DingUtils;
import com.taobao.api.ApiException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * @author hp
 */
@Slf4j
public class DingUserHandler extends AbstractDingApiHandler implements IDingUserHandler {
    public static final Pattern PHONE_PATTERN = Pattern.compile(RegexPool.MOBILE);

    public DingUserHandler(@NonNull IDingApp app) {
        super(app);
    }

    @Override
    public String findUserIdByMobile(@NonNull String mobile) {
        Preconditions.checkArgument(PHONE_PATTERN.matcher(mobile).matches(), "非法手机号");
        OapiV2UserGetbymobileRequest request = new OapiV2UserGetbymobileRequest();
        request.setMobile(mobile);
        request.setSupportExclusiveAccountSearch(false);
        final OapiV2UserGetbymobileResponse response = execute(
                DingUrlConstant.User.GET_USERID_BY_MOBILE,
                request,
                resp -> {
                    try {
                        DingUtils.UserResponse.isOk(resp);
                    } catch (ApiException e) {
                        log.error("根据电话获取钉钉userId响应失败", e);
                        throw new DingApiException("根据电话获取钉钉userId响应失败");
                    }
                },
                () -> "根据电话获取钉钉userId"
        );
        return response.getResult().getUserid();
    }

    @Override
    public String findUnionIdByCode(@NonNull String code) {
        OapiSnsGetuserinfoBycodeRequest request = new OapiSnsGetuserinfoBycodeRequest();
        request.setTmpAuthCode(code);
        final OapiSnsGetuserinfoBycodeResponse response = execute(
                DingUrlConstant.User.GET_USER_INFO_BY_CODE,
                request,
                resp -> {
                    try {
                        DingUtils.UserResponse.isOk(resp);
                    } catch (ApiException e) {
                        log.error("根据code获取钉钉unionId响应失败", e);
                        throw new DingApiException("根据code获取钉钉unionId响应失败");
                    }
                },
                () -> "根据code获取钉钉unionId",
                false
        );
        return response.getUserInfo().getUnionid();
    }

    @Override
    public String findUserIdByUnionId(@NonNull String unionId) {
        OapiUserGetbyunionidRequest request = new OapiUserGetbyunionidRequest();
        request.setUnionid(unionId);
        OapiUserGetbyunionidResponse response = execute(
                DingUrlConstant.User.GET_USER_ID_BY_UNION_ID,
                request,
                resp -> {
                    try {
                        DingUtils.UserResponse.isOk(resp);
                    } catch (ApiException e) {
                        log.error("根据unionId获取钉钉userId响应失败", e);
                        throw new DingApiException("根据unionId获取钉钉userId响应失败");
                    }
                },
                () -> "根据unionId获取钉钉userId"
        );
        return response.getResult().getUserid();
    }

    @Override
    public OapiV2UserGetResponse.UserGetResponse findUserByUserId(@NonNull String userId) {
        OapiV2UserGetRequest request = new OapiV2UserGetRequest();
        request.setUserid(userId);
        request.setLanguage(Language.zh_CN.name());
        OapiV2UserGetResponse response = execute(DingUrlConstant.User.GET_USER_BY_USER_ID,
                request,
                resp -> {
                    try {
                        DingUtils.UserResponse.isOk(resp);
                    } catch (ApiException e) {
                        log.error("根据userId获取钉钉用户响应失败", e);
                        throw new DingApiException("根据userId获取钉钉用户响应失败");
                    }
                },
                () -> "根据userId获取钉钉用户"
        );
        return response.getResult();
    }

    @Override
    public OapiV2UserGetResponse.UserGetResponse findUserByMobile(@NonNull String mobile) {
        final String userId = this.findUserIdByMobile(mobile);
        return this.findUserByUserId(userId);
    }

    @Override
    public OapiV2UserGetResponse.UserGetResponse findUserByCode(@NonNull String code) {
        final String unionId = findUnionIdByCode(code);
        final String userId = findUserIdByUnionId(unionId);
        return findUserByUserId(userId);
    }

    @Override
    public OapiV2UserGetuserinfoResponse.UserGetByCodeResponse findUserByLoginAuthCode(@NonNull String authCode) {
        OapiV2UserGetuserinfoRequest request = new OapiV2UserGetuserinfoRequest();
        request.setCode(authCode);
        OapiV2UserGetuserinfoResponse response = execute(
                DingUrlConstant.User.GET_USER_BY_LOGIN_AUTH_CODE,
                request,
                resp -> {
                    try {
                        DingUtils.UserResponse.isOk(resp);
                    } catch (ApiException e) {
                        log.error("根据authCode获取钉钉用户响应失败", e);
                        throw new DingApiException("根据authCode获取钉钉用户响应失败");
                    }
                },
                () -> "根据authCode获取钉钉用户"
        );
        return response.getResult();
    }

    @Override
    public OapiUserListsimpleResponse.PageResult findNameAndUserIdByDept(Long deptId, Long cursor, Long size) {
        return findNameAndUserIdByDept(deptId, cursor, size, DeptUserOrder.custom, true, Language.zh_CN);
    }

    @Override
    public OapiUserListsimpleResponse.PageResult findNameAndUserIdByDept(
            Long deptId,
            Long cursor,
            Long size,
            DeptUserOrder order,
            Boolean containAccessLimit,
            Language language
    ) {
        OapiUserListsimpleRequest request = new OapiUserListsimpleRequest();
        request.setDeptId(deptId);
        request.setCursor(cursor);
        request.setSize(size);
        request.setOrderField(order.name());
        request.setContainAccessLimit(containAccessLimit);
        request.setLanguage(language.name());
        OapiUserListsimpleResponse response = execute(
                DingUrlConstant.User.GET_USER_BASE_INFO_IN_DEPT,
                request,
                () -> "根据部门Id获取用户名称和userId"
        );
        return response.getResult();
    }
}
