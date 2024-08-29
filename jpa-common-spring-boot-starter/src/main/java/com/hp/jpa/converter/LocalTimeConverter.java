package com.hp.jpa.converter;

import cn.hutool.core.date.DatePattern;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalTime;
import java.util.Optional;

/**
 * @author hp 2023/4/10
 */
@Converter
public class LocalTimeConverter implements AttributeConverter<LocalTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalTime attribute) {
       return Optional.ofNullable(attribute).map(a->a.format(DatePattern.NORM_TIME_FORMATTER)).orElse(null);
    }

    @Override
    public LocalTime convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(a->LocalTime.parse(a, DatePattern.NORM_TIME_FORMATTER)).orElse(null);
    }
}
