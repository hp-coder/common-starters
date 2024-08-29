package com.hp.dingtalk.constant;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp 2023/3/21
 */
public interface DingApiCode<T extends Enum<T> & DingApiCode<T>> extends BaseEnum<T, Long> {
    static Optional<UserResponse> ofUserResponse(Long code) {
        return Optional.ofNullable(BaseEnum.parseByCode(UserResponse.class, code));
    }
    static Optional<BaseResponse> ofBaseResponse(Long code) {
        return Optional.ofNullable(BaseEnum.parseByCode(BaseResponse.class, code));
    }
    @Getter
    @AllArgsConstructor
    enum BaseResponse implements DingApiCode<BaseResponse> {
        // 基础接口相关
        IP_NOT_IN_WHITE_LIST(88L, "IP不在白名单");
        private final Long code;
        private final String name;
    }

    @Getter
    @AllArgsConstructor
    enum UserResponse implements DingApiCode<UserResponse> {
        // 用户管理接口相关
        NOT_FOUND(60121L, "找不到该用户");
        private final Long code;
        private final String name;
    }
}
