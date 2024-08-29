package com.hp.jpa.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;
import java.util.Optional;

/**
 * @author hp
 * @date 2022/10/19
 */
@Converter
public class InstantLongConverter implements AttributeConverter<Instant, Long> {
    @Override
    public Long convertToDatabaseColumn(Instant date) {
        return Optional.ofNullable(date).map(Instant::toEpochMilli).orElse(null);
    }

    @Override
    public Instant convertToEntityAttribute(Long date) {
        return Optional.ofNullable(date).map(Instant::ofEpochMilli).orElse(null);
    }
}

