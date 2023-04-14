package com.luban.jpa.convertor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author hp 2023/4/10
 */
@Converter
public class LocalDateConverter implements AttributeConverter<LocalDate, String> {

    @Override
    public String convertToDatabaseColumn(LocalDate attribute) {
       return Optional.ofNullable(attribute).map(a->a.format(DateTimeFormatter.ISO_DATE)).orElse(null);
    }

    @Override
    public LocalDate convertToEntityAttribute(String dbData) {
        return Optional.ofNullable(dbData).map(a->LocalDate.parse(a, DateTimeFormatter.ISO_DATE)).orElse(null);
    }
}
