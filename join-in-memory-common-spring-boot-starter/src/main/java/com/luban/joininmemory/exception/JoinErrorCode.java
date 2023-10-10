package com.luban.joininmemory.exception;

import com.luban.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum JoinErrorCode implements BaseEnum<JoinErrorCode, Integer> {

    /***/
    ERROR(700500, "Join异常"),
    ;

    private final Integer code;
    private final String name;

    @Override
    public String toString() {
        return String.format("JoinError [%s]:%s", getCode(), getName());
    }
}
