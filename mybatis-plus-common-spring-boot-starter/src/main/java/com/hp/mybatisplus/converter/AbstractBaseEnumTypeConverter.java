package com.hp.mybatisplus.converter;


import com.hp.common.base.enums.BaseEnum;
import com.hp.common.base.enums.BaseEnumBasedAdapter;
import com.hp.mybatisplus.annotation.Converter;

import java.util.Optional;

/**
 * @author hp
 */
@Converter
public abstract class AbstractBaseEnumTypeConverter<T extends Enum<T> & BaseEnum<T, E>, E> implements TypeHandlerAdapter<T, E>, BaseEnumBasedAdapter<T,E> {

    @Override
    public E fieldToColumn(T t) {
        return Optional.ofNullable(t).map(BaseEnum::getCode).orElse(null);
    }

    @Override
    public T columnToField(E code) {
        return Optional.ofNullable(code).map(c -> BaseEnum.parseByCode(getBaseEnumType(), c)).orElse(null);
    }
}
