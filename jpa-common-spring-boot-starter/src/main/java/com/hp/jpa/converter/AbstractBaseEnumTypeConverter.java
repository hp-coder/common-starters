package com.hp.jpa.converter;


import com.hp.common.base.enums.BaseEnum;
import com.hp.common.base.enums.BaseEnumBasedAdapter;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
@Converter
public abstract class AbstractBaseEnumTypeConverter<T extends Enum<T> & BaseEnum<T, E>, E> implements AttributeConverter<T, E>, BaseEnumBasedAdapter<T, E> {
    protected final Class<T> baseEnumType = Objects.requireNonNull(getBaseEnumType());

    @Override
    public E convertToDatabaseColumn(T attribute) {
        return Optional.ofNullable(attribute).map(BaseEnum::getCode).orElse(null);
    }

    @Override
    public T convertToEntityAttribute(E dbData) {
        return Optional.ofNullable(dbData).map(data -> BaseEnum.parseByCode(baseEnumType, data)).orElse(null);
    }
}
