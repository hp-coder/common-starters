package com.luban.common.base.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class DateMapper {
    public Long asLong(Instant date) {
        if (Objects.nonNull(date)) {
            return date.toEpochMilli();
        }
        return null;
    }

    public Instant asInstant(Long date) {
        if (Objects.nonNull(date)) {
            return Instant.ofEpochMilli(date);
        }
        return null;
    }

    public String asString(LocalDate date) {
        if (Objects.nonNull(date)) {
            return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return null;
    }

    public LocalDate asLocalDate(String date) {
        if (Objects.nonNull(date)) {
            try {
                return LocalDate.parse(date);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
