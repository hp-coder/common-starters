package com.luban.spiderman.constants;


import com.luban.common.base.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

/**
 * @author hp
 */
@Getter
@AllArgsConstructor
public enum FilterLogic implements BaseEnum<FilterLogic, String> {
    /***/
    exist("","元素存在"),
    equals("=","等于"),
    contains("*=","包含"),
    containsWithSpaceSeparated("~=","包含空格分隔的元素"),
    endsWith("$=","以结尾"),
    startsWith("^=","以开头"),
    startsWithValueDash("|=","以value-开头"),
    ;
    private final String code;
    private final String name;

    public static Optional<FilterLogic> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(FilterLogic.class, code));
    }
}
