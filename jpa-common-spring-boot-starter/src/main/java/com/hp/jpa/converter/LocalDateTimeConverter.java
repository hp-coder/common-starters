package com.hp.jpa.converter;

import cn.hutool.core.date.DatePattern;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp 2023/4/10
 */
@Converter
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    protected DateTimeFormatter formatter = DatePattern.NORM_DATETIME_FORMATTER;

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return Optional.ofNullable(attribute).map(a -> a.format(formatter)).orElse(null);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(a -> LocalDateTime.parse(a, formatter)).orElse(null);
    }
}
