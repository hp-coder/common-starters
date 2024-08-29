package com.hp.jpa.converter;


import cn.hutool.core.util.StrUtil;
import com.hp.common.base.enums.BaseEnum;
import jakarta.persistence.Converter;

import java.util.Collection;

/**
 * @author hp
 */
@Converter
public abstract class StringBasedBaseEnumTypesConverter<T extends Enum<T> & BaseEnum<T, String>, FIELD extends Collection<T>> extends AbstractBaseEnumTypesConverter<T, String, FIELD> {

    public StringBasedBaseEnumTypesConverter() {
        super(StrUtil.COMMA);
    }

    public StringBasedBaseEnumTypesConverter(String separator) {
        super(separator);
    }
}
