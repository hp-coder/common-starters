package com.luban.common.base.mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

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

    public String instantToString(Instant instant){
        return Optional.ofNullable(instant)
                .map(i-> LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Shanghai")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .orElse(null);
    }

    public Instant stringToInstant(String datetime){
        return Optional.ofNullable(datetime)
                .map(i-> LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.of("Asia/Shanghai")).toInstant())
                .orElse(null);
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

    public String asString(LocalDateTime date) {
        if (Objects.nonNull(date)) {
            return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return null;
    }

    public LocalDateTime asLocalDateTime(String date) {
        if (Objects.nonNull(date)) {
            try {
                return LocalDateTime.parse(date,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}
