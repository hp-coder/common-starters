package com.hp.jpa.convertor;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Instant;

/**
 * @author HP
 * @date 2022/10/19
 */
@Converter
public class InstantLongConverter implements AttributeConverter<Instant, Long> {
    @Override
    public Long convertToDatabaseColumn(Instant date) {
        return date == null ? null : date.toEpochMilli();
    }

    @Override
    public Instant convertToEntityAttribute(Long date) {
        return date == null ? null : Instant.ofEpochMilli(date);
    }
}

