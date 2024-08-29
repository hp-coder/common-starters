package com.hp.dingtalk.service;

import com.aliyun.dingtalkcontact_1_0.models.GetUserResponseBody;
import lombok.NonNull;

/**
 * @author hp 2023/3/23
 */
public interface IDingContactHandler {
    /**
     * 获取用户通讯录个人信息
     *
     * @param userToken 用户token，一般在登录时获取
     * @param unionId   用户unionId或 'me'，传'me'时通过登录时授权获取的userToken做免密登录
     */
    GetUserResponseBody personalContactInfo(@NonNull String userToken, @NonNull String unionId);
}
