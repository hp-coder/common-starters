package com.hp.biz.logger.exception;

import com.hp.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum BizLoggerErrorCode implements BaseEnum<BizLoggerErrorCode, Integer> {
    /***/
    async_creation_error(10099_001,"异步创建操作日志异常"),
    ;
    private final Integer code;
    private final String name;

    public static Optional<BizLoggerErrorCode> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(BizLoggerErrorCode.class, code));
    }

    public static Optional<BizLoggerErrorCode> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }
}
