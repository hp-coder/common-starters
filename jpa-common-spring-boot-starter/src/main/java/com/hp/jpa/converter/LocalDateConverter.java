package com.hp.jpa.converter;

import cn.hutool.core.date.DatePattern;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp 2023/4/10
 */
@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    protected DateTimeFormatter formatter = DatePattern.NORM_DATE_FORMATTER;

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
        return Optional.ofNullable(attribute).map(a -> a.format(formatter)).orElse(null);
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(a -> LocalDate.parse(a, formatter)).orElse(null);
    }
}
