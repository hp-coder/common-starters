package com.hp.dingtalk.service;

import com.aliyun.dingtalkoauth2_1_0.models.GetUserTokenResponseBody;
import lombok.NonNull;

/**
 * @author hp 2023/3/23
 */
public interface IDingLoginHandler {

    enum GrantType {
        /**
         * 如果使用授权码换token，传authorization_code
         */
        authorization_code,
        /**
         * 如果使用刷新token换用户token，传refresh_token
         */
        refresh_token
    }

    /**
     * 登录时通过钉钉回调获取的授权码换取用户token，常用在登录
     *
     * @param authCode 授权码
     * @return userToken
     * @throws Exception 客户端实例化异常
     */
    GetUserTokenResponseBody getUserToken(@NonNull String authCode, @NonNull GrantType grantType) throws Exception;
}
