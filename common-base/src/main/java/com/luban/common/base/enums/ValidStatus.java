package com.luban.common.base.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Optional;

@Getter
@AllArgsConstructor
public enum ValidStatus implements BaseEnum<ValidStatus> {

    VALID(1, "valid"),
    INVALID(0, "invalid");

    private Integer code;
    private String name;

    public static Optional<ValidStatus> of(Integer code){
        return Optional.ofNullable(BaseEnum.parseByCode(ValidStatus.class,code));
    }
}
