package com.hp.codegen.constant;

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
public enum GenerateTarget implements BaseEnum<GenerateTarget, String> {
    /***/
    COMPILE("compile", "字节码目录"),
    SOURCE("source", "源码目录"),
    ;
    private final String code;
    private final String name;

    public static Optional<GenerateTarget> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(GenerateTarget.class, code));
    }

    public static Optional<GenerateTarget> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }
}
