package com.luban.joininmemory.exception;

import com.luban.common.base.enums.BaseEnum;
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
public enum JoinErrorCode implements BaseEnum<JoinErrorCode, Integer> {

    /***/
    ERROR(700500, "Join异常"),

    ;

    private final Integer code;
    private final String name;

    public static Optional<JoinErrorCode> of(Integer code) {
        return Optional.ofNullable(BaseEnum.parseByCode(JoinErrorCode.class, code));
    }

    public static Optional<JoinErrorCode> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }

    @Override
    public String toString() {
        return String.format("JoinError [%s]:%s", getCode(), getName());
    }
}
