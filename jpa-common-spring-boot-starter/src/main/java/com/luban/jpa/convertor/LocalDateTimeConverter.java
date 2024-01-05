package com.luban.jpa.convertor;

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

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return Optional.ofNullable(attribute).map(a -> a.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).orElse(null);
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(a -> LocalDateTime.parse(a, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).orElse(null);
    }
}
