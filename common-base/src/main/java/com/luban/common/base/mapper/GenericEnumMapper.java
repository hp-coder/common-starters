package com.luban.common.base.mapper;


import com.luban.common.base.enums.ValidStatus;

public class GenericEnumMapper {

    public Integer asInteger(ValidStatus status) {
        return status.getCode();
    }

    public ValidStatus asValidStatus(Integer code) {
        return ValidStatus.of(code).orElse(ValidStatus.INVALID);
    }

}
