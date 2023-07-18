package com.hp.jpa.convertor;

import com.hp.common.base.enums.ValidStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author hp 2023/4/10
 */
@Converter
public class ValidStatusConverter implements AttributeConverter<ValidStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ValidStatus attribute) {
       return attribute.getCode();
    }

    @Override
    public ValidStatus convertToEntityAttribute(Integer dbData) {
        return ValidStatus.of(dbData).orElse(null);
    }
}
