package com.hp.jpa.converter;

import com.hp.common.base.valueobject.AbstractSingleValueObject;
import com.hp.common.base.valueobject.AbstractStringBasedSingleValueObject;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Optional;

/**
 * @author hp 2023/4/10
 */
@Converter
public abstract class AbstractStringBasedSingleValueObjectConverter<T extends AbstractStringBasedSingleValueObject> implements AttributeConverter<T, String> {

    @Override
    public String convertToDatabaseColumn(T attribute) {
        return Optional.ofNullable(attribute).map(AbstractSingleValueObject::value).orElse(null);
    }
}
