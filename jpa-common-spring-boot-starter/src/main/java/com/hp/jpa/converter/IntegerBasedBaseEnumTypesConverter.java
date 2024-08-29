package com.hp.jpa.converter;


import cn.hutool.core.util.StrUtil;
import com.hp.common.base.enums.BaseEnum;
import jakarta.persistence.Converter;

import java.util.Collection;

/**
 * @author hp
 */
@Converter
public abstract class IntegerBasedBaseEnumTypesConverter<T extends Enum<T> & BaseEnum<T, Integer>, FIELD extends Collection<T>> extends AbstractBaseEnumTypesConverter<T, Integer, FIELD> {

    public IntegerBasedBaseEnumTypesConverter() {
        super(StrUtil.COMMA);
    }

    public IntegerBasedBaseEnumTypesConverter(String separator) {
        super(separator);
    }
}
