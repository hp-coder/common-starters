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
public enum MappingMode implements BaseEnum<MappingMode, String> {
    /***/
    MapStruct("mapstruct", "MapStruct"),
    Jackson("jackson","Jackson"),
    ;
    private final String code;
    private final String name;

    public static Optional<MappingMode> of(String code) {
        return Optional.ofNullable(BaseEnum.parseByCode(MappingMode.class, code));
    }

    public static Optional<MappingMode> ofName(String name) {
        return Arrays.stream(values())
                .filter(i -> Objects.equals(name, i.getName()))
                .findFirst();
    }
}
